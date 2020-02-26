package com.gmail.ayteneve93.blueberrysherbetcore.request

import android.bluetooth.BluetoothGattCharacteristic
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.databinding.ObservableField
import com.gmail.ayteneve93.blueberrysherbetannotations.INDICATE
import com.gmail.ayteneve93.blueberrysherbetannotations.NOTIFY
import com.gmail.ayteneve93.blueberrysherbetannotations.READ
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
import com.gmail.ayteneve93.blueberrysherbetcore.utility.BlueberryLogger
import com.squareup.moshi.Moshi
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Suppress("UNCHECKED_CAST")
fun <ReturnType>convertStringToObject(returnType : Class<out Any>, dataString : String, moshi: Moshi) : ReturnType? {
    return when(returnType) {
        String::class.java -> dataString as? ReturnType
        Long::class.java -> dataString.toLong() as? ReturnType
        Int::class.java -> dataString.toInt() as? ReturnType
        Short::class.java -> dataString.toShort() as? ReturnType
        Byte::class.java -> dataString.toByte() as? ReturnType
        Char::class.java -> dataString[0] as? ReturnType
        Double::class.java -> dataString.toDouble() as? ReturnType
        Float::class.java -> dataString.toFloat() as? ReturnType
        Boolean::class.java -> dataString.toBoolean() as? ReturnType
        else -> moshi.adapter<ReturnType>(returnType).fromJson(dataString)
    }
}

abstract class BlueberryRequestInfo(
    internal val uuid : UUID,
    internal val priority : Int,
    internal var awaitingMills : Int,
    internal val blueberryRequest : BlueberryRequest<out Any>,
    internal val requestType : Class<out Annotation>
) {
    internal val requestCode : Long = requestCount++
    internal var isOnProgress : Boolean = false
    internal val requestTimer = Handler(Looper.getMainLooper())
    open fun cancel() = blueberryRequest.mBlueberryDevice.cancelBlueberryRequest(this)
    fun startTimer() {
        requestTimer.postDelayed({
            blueberryRequest.mBlueberryDevice.disconnect()
        }, awaitingMills.toLong())
    }
    internal open fun onResponse(status : Int?, characteristic : BluetoothGattCharacteristic?) {
        requestTimer.removeCallbacksAndMessages(null)
    }
    companion object { private var requestCount : Long = 0L }
}

typealias BlueberryCallbackWithResult<ReturnType> = (status : Int, value : ReturnType?) -> Unit
data class BlueberryCallbackResultData<ReturnType>(val status : Int, val value : ReturnType?)
@Suppress("SpellCheckingInspection", "UNCHECKED_CAST")
class BlueberryRequestInfoWithSimpleResult<ReturnType>(
    uuid : UUID,
    priority: Int,
    awaitingMills: Int,
    blueberryRequest: BlueberryRequest<ReturnType>,
    requestType : Class<out Annotation>
) : BlueberryRequestInfo(uuid, priority, awaitingMills, blueberryRequest as BlueberryRequest<out Any>, requestType) {
    private lateinit var callback : BlueberryCallbackWithResult<ReturnType>

    fun enqueue(callback : BlueberryCallbackWithResult<ReturnType>) {
        this.callback = callback
        blueberryRequest.mBlueberryDevice.enqueueBlueberryRequestInfo(this)
    }
    fun byRx2() : Single<BlueberryCallbackResultData<ReturnType>> = Single.create { emitter -> enqueue { status, value -> emitter.onSuccess(BlueberryCallbackResultData(status, value))} }
    suspend fun byCoroutine() : BlueberryCallbackResultData<ReturnType> = suspendCoroutine { continuation -> enqueue { status, value -> continuation.resume(BlueberryCallbackResultData(status, value)) } }
    override fun onResponse(status: Int?, characteristic: BluetoothGattCharacteristic?) {
        super.onResponse(status, characteristic)
        val dataString = characteristic?.getStringValue(0)
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

@Suppress("SpellCheckingInspection", "UNCHECKED_CAST")
class BlueberryRequestInfoWithRepetitiousResults<ReturnType>(
    uuid : UUID,
    priority : Int,
    awaitingMills : Int,
    blueberryRequest : BlueberryRequest<ReturnType>,
    requestType : Class<out Annotation>,
    private val startString : String,
    private val endString : String
) : BlueberryRequestInfo(uuid, priority, awaitingMills, blueberryRequest as BlueberryRequest<out Any>, requestType) {

    internal var isNotificationEnabled = true
    private lateinit var callback : BlueberryCallbackWithResult<ReturnType>

    override fun cancel() {
        isNotificationEnabled = false
        super.cancel()
    }

    fun enqueue(callback : BlueberryCallbackWithResult<ReturnType>) {
        this.callback = callback
        blueberryRequest.mBlueberryDevice.enqueueBlueberryRequestInfo(this)
    }

    fun byRx2() : Observable<BlueberryCallbackResultData<ReturnType>>
        = Observable.create<BlueberryCallbackResultData<ReturnType>> { emitter ->
            enqueue { status, value -> emitter.onNext(BlueberryCallbackResultData(status, value)) }
        }

    fun byDataBinding() : ObservableField<ReturnType> = ObservableField<ReturnType>().apply {
        enqueue { _, value ->  value?.let { set(it) } }
    }

    private var synthesizedByteArrayList : ArrayList<Byte> = ArrayList()
    override fun onResponse(status: Int?, characteristic: BluetoothGattCharacteristic?) {
        super.onResponse(status, characteristic)
        characteristic?.value?.let { partialData ->
            when(String(partialData)) {
                startString -> return@let
                endString -> {
                    try {
                        callback.invoke(0, when(requestType) {
                            NOTIFY::class.java, INDICATE::class.java -> convertStringToObject<ReturnType>(
                                blueberryRequest.mReturnTypeClass,
                                String(synthesizedByteArrayList.toTypedArray().toByteArray()),
                                blueberryRequest.mMoshi)
                            else -> null
                        })
                        synthesizedByteArrayList.clear()
                    } catch(exception : Exception) { BlueberryLogger.e("Exception Occured While Parsing Data String", exception) }
                }
                else -> synthesizedByteArrayList.addAll(partialData.toList())
            }
        }
    }

}

typealias BlueberryCallbackWithoutResult = (status : Int) -> Unit
@Suppress("SpellCheckingInspection")
class BlueberryRequestInfoWithoutResult(
    uuid : UUID,
    priority: Int,
    awaitingMills: Int,
    blueberryRequest: BlueberryRequest<out Any>,
    requestType : Class<out Annotation>,
    internal val inputString : String?
) : BlueberryRequestInfo(uuid, priority, awaitingMills, blueberryRequest, requestType) {
    private lateinit var callback : BlueberryCallbackWithoutResult
    fun enqueue(callback : BlueberryCallbackWithoutResult) {
        this.callback = callback
        blueberryRequest.mBlueberryDevice.enqueueBlueberryRequestInfo(this)
    }
    fun byRx2() : Single<Int> = Single.create { emitter -> enqueue { emitter.onSuccess(it) } }
    suspend fun byCoroutine() : Int = suspendCoroutine { continuation -> enqueue { continuation.resume(it) } }
    override fun onResponse(status: Int?, characteristic: BluetoothGattCharacteristic?) {
        super.onResponse(status, characteristic)
        callback.invoke(status!!)
    }
}

@Suppress("SpellCheckingInspection")
class BlueberryRequestInfoWithNoResponse(
    uuid : UUID,
    priority : Int,
    awaitingMills: Int,
    blueberryRequest : BlueberryRequest<out Any>,
    requestType : Class<out Annotation>,
    internal val inputString : String?
) : BlueberryRequestInfo(uuid, priority, awaitingMills, blueberryRequest, requestType) {
    fun enqueue() {
        blueberryRequest.mBlueberryDevice.enqueueBlueberryRequestInfo(this)
    }
}

@Suppress("SpellCheckingInspection")
abstract class BlueberryRequest<ReturnType>
    constructor(

        internal val mReturnTypeClass : Class<ReturnType>,
        internal var mMoshi : Moshi,
        internal val mBlueberryDevice: BlueberryDevice<out Any>,
        internal val mPriority : Int,

        uuidString : String){

    internal val mUuid = UUID.fromString(uuidString)

    fun addMoshiAdapters(vararg adapters : Any) {
        mMoshi = mMoshi.newBuilder().apply {
            adapters.forEach { this.add(it) }
        }.build()
    }

}