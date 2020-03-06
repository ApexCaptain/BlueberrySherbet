package com.gmail.ayteneve93.blueberrysherbetcore.request

import android.bluetooth.BluetoothGattCharacteristic
import android.os.Build
import com.gmail.ayteneve93.blueberrysherbetannotations.READ
import com.gmail.ayteneve93.blueberrysherbetcore.utility.BlueberryLogger
import io.reactivex.Single
import java.util.*
import kotlin.collections.HashMap
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Suppress("SpellCheckingInspection", "UNCHECKED_CAST")
class BlueberryRequestInfoWithSimpleResult<ReturnType>(
    uuid : UUID,
    priority: Int,
    awaitingMills: Int,
    blueberryRequest: BlueberryAbstractRequest<ReturnType>,
    requestType : Class<out Annotation>
) : BlueberryAbstractRequestInfo(uuid, priority, awaitingMills, blueberryRequest as BlueberryAbstractRequest<out Any>, requestType) {
    private lateinit var callback : BlueberryCallbackWithResult<ReturnType>

    override fun convertToSimpleHashMap(): HashMap<String, Any?> = super.convertToSimpleHashMap().apply {
        this["Return Type"] = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) blueberryRequest.mReturnTypeClass.typeName
        else blueberryRequest.mReturnTypeClass.simpleName
    }

    fun enqueue(callback : BlueberryCallbackWithResult<ReturnType>) {
        this.callback = callback
        blueberryRequest.mBlueberryDevice.enqueueBlueberryRequestInfo(this)
    }
    fun byRx2() : Single<BlueberryCallbackResultData<ReturnType>> = Single.create { emitter -> enqueue { status, value -> emitter.onSuccess(BlueberryCallbackResultData(status, value))} }
    suspend fun byCoroutine() : BlueberryCallbackResultData<ReturnType> = suspendCoroutine { continuation -> enqueue { status, value -> continuation.resume(BlueberryCallbackResultData(status, value)) } }
    override fun onResponse(status: Int?, characteristic: BluetoothGattCharacteristic?) {
        super.onResponse(status, characteristic)
        try {
            callback.invoke(status!!, with(characteristic?.getStringValue(0)) {
                if(this.isNullOrEmpty()) null
                else when(requestType) {
                    READ::class.java -> convertStringToObject<ReturnType>(
                        blueberryRequest.mReturnTypeClass,
                        this,
                        blueberryRequest.mMoshi)
                    else -> null
                }
            })
        } catch(exception : Exception) { BlueberryLogger.e("Exception Occured While Parsing Data String", exception) }
    }
}