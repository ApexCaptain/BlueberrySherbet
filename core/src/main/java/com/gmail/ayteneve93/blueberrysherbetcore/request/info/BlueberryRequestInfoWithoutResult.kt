package com.gmail.ayteneve93.blueberrysherbetcore.request.info

import android.bluetooth.BluetoothGattCharacteristic
import android.util.Log
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryAbstractRequest
import io.reactivex.Single
import java.util.*
import kotlin.collections.HashMap
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Suppress("SpellCheckingInspection")
class BlueberryRequestInfoWithoutResult(
    uuid : UUID,
    priority: Int,
    awaitingMills: Int,
    blueberryRequest: BlueberryAbstractRequest<out Any>,
    requestType : Class<out Annotation>,
    internal val inputString : String?,
    val checkIsReliable : Boolean
) : BlueberryAbstractRequestInfo(
    mUuid = uuid,
    mPriority = priority,
    mAwaitingMills = awaitingMills,
    mBlueberryRequest = blueberryRequest,
    mRequestType = requestType) {
    private lateinit var callback : BlueberryCallbackWithoutResult

    override fun convertToSimpleHashMap(): HashMap<String, Any?> = super.convertToSimpleHashMap().apply {
        this["Input Data"] = inputString
        this["Use Reliable Write"] = checkIsReliable
    }

    fun enqueue(callback : BlueberryCallbackWithoutResult) {
        this.callback = callback
        mBlueberryRequest.mBlueberryDevice.enqueueBlueberryRequestInfo(this)
    }

    fun byRx2() : Single<Int> = Single.create { emitter -> enqueue { emitter.onSuccess(it) } }
    suspend fun byCoroutine() : Int = suspendCoroutine { continuation -> enqueue { continuation.resume(it) } }
    override fun onResponse(status: Int?, characteristic: BluetoothGattCharacteristic?) {
        super.onResponse(status, characteristic)
        callback.invoke(status!!)
    }
}