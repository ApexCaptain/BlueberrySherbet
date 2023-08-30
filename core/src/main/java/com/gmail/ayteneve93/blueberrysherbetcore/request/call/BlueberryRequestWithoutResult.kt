package com.gmail.ayteneve93.blueberrysherbetcore.request.call

import android.bluetooth.BluetoothGattCharacteristic
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryAbstractRequestInfo
import io.reactivex.Single
import java.util.*
import kotlin.collections.HashMap
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BlueberryRequestWithoutResult(
    uuid : UUID,
    priority: Int,
    awaitingMills: Int,
    blueberryRequestInfo: BlueberryAbstractRequestInfo<out Any>,
    requestType : Class<out Annotation>,
    internal val inputString : String?,
    internal val inputBytes: ByteArray?,
    val checkIsReliable : Boolean,
    val useSimpleBytes : Boolean
) : BlueberryAbstractRequest(
    mUuid = uuid,
    mPriority = priority,
    mAwaitingMills = awaitingMills,
    mBlueberryRequestInfo = blueberryRequestInfo,
    mRequestType = requestType) {
    private lateinit var callback : BlueberryCallbackWithoutResult

    override fun convertToSimpleHashMap(): HashMap<String, Any?> = super.convertToSimpleHashMap().apply {
        this["Input Data"] = if(useSimpleBytes) inputBytes else inputString
        this["Use Reliable Write"] = checkIsReliable
    }

    fun enqueue(callback : BlueberryCallbackWithoutResult) {
        this.callback = callback
        mBlueberryRequestInfo.mBlueberryDevice.enqueueBlueberryRequestInfo(this)
    }

    fun byRx2() : Single<Int> = Single.create { emitter -> enqueue { emitter.onSuccess(it) } }
    suspend fun byCoroutine() : Int = suspendCoroutine { continuation -> enqueue { continuation.resume(it) } }
    override fun onResponse(status: Int?, characteristic: BluetoothGattCharacteristic?) {
        super.onResponse(status, characteristic)
        callback.invoke(status!!)
    }
}