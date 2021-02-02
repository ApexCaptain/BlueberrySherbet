package com.gmail.ayteneve93.blueberrysherbetcore.request.call

import android.bluetooth.BluetoothGattCharacteristic
import android.os.Build
import com.gmail.ayteneve93.blueberrysherbetannotations.READ
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryAbstractRequestInfo
import com.gmail.ayteneve93.blueberrysherbetcore.utility.BlueberryLogger
import io.reactivex.Single
import java.util.*
import kotlin.collections.HashMap
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Suppress("UNCHECKED_CAST")
class BlueberryRequestWithSimpleResult<ReturnType>(
    uuid : UUID,
    priority: Int,
    awaitingMills: Int,
    blueberryRequestInfo: BlueberryAbstractRequestInfo<ReturnType>,
    requestType : Class<out Annotation>
) : BlueberryAbstractRequest(
    mUuid = uuid,
    mPriority = priority,
    mAwaitingMills = awaitingMills,
    mBlueberryRequestInfo = blueberryRequestInfo as BlueberryAbstractRequestInfo<out Any>,
    mRequestType = requestType) {
    private lateinit var callback : BlueberryCallbackWithResult<ReturnType>

    override fun convertToSimpleHashMap(): HashMap<String, Any?> = super.convertToSimpleHashMap().apply {
        this["Return Type"] = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) mBlueberryRequestInfo.mReturnTypeClass.typeName
        else mBlueberryRequestInfo.mReturnTypeClass.simpleName
    }

    fun enqueue(callback : BlueberryCallbackWithResult<ReturnType>) {
        this.callback = callback
        mBlueberryRequestInfo.mBlueberryDevice.enqueueBlueberryRequestInfo(this)
    }
    fun byRx2() : Single<BlueberryCallbackResultData<ReturnType>> = Single.create { emitter -> enqueue { status, value -> emitter.onSuccess(
        BlueberryCallbackResultData(
            status,
            value
        )
    )} }
    suspend fun byCoroutine() : BlueberryCallbackResultData<ReturnType> = suspendCoroutine { continuation -> enqueue { status, value -> continuation.resume(
        BlueberryCallbackResultData(
            status,
            value
        )
    ) } }
    override fun onResponse(status: Int?, characteristic: BluetoothGattCharacteristic?) {
        super.onResponse(status, characteristic)
        try {
            callback.invoke(status!!, with(characteristic?.getStringValue(0)) {
                if(this.isNullOrEmpty()) null
                else when(mRequestType) {
                    READ::class.java -> mBlueberryRequestInfo.blueberryConverter.parse(
                        this, mBlueberryRequestInfo.mReturnTypeClass as Class<ReturnType>
                    )
                    else -> null
                }
            })
        } catch(exception : Exception) { BlueberryLogger.e("Exception Occurred While Parsing Data String", exception) }
    }
}