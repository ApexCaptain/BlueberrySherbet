package com.gmail.ayteneve93.blueberrysherbetcore.request

import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
import com.squareup.moshi.Moshi
import io.reactivex.Single
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

abstract class BlueberryRequestInfo(
    internal val uuid : UUID,
    internal val priority : Int,
    internal val awaitingMills : Int,
    internal val blueberryRequest : BlueberryRequest<out Any>,
    internal val requestType : Class<out Annotation>
) {
    internal val requestCode : Long = requestCount++
    internal var isOnProgress : Boolean = false
    internal abstract fun onResponse(status : Int, dataString : String?)
    companion object { private var requestCount : Long = 0L }
}

typealias BlueberryCallbackWithResult<ReturnType> = (status : Int, value : ReturnType?) -> Unit
class BlueberryCallbackResult<ReturnType>(status : Int, value : ReturnType?)
@Suppress("SpellCheckingInspection")
class BlueberryRequestInfoWithResult<ReturnType>(
    uuid : UUID,
    priority: Int,
    awaitingMills: Int,
    blueberryRequest: BlueberryRequest<out Any>,
    requestType : Class<out Annotation>
) : BlueberryRequestInfo(uuid, priority, awaitingMills, blueberryRequest, requestType) {
    private lateinit var callback : BlueberryCallbackWithResult<ReturnType>
    fun enqueue(callback : BlueberryCallbackWithResult<ReturnType>) {
        this.callback = callback
        blueberryRequest.mBlueberryDevice.enqueueBlueberryRequestInfo(this)
    }
    fun byRx2() : Single<BlueberryCallbackResult<ReturnType>> = Single.create { emitter -> enqueue { status, value -> emitter.onSuccess(BlueberryCallbackResult(status, value))} }
    suspend fun byCoroutine() : BlueberryCallbackResult<ReturnType> = suspendCoroutine { continuation -> enqueue { status, value -> continuation.resume(BlueberryCallbackResult(status, value)) } }
    override fun onResponse(status: Int, dataString: String?) {
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
    override fun onResponse(status: Int, dataString: String?) = callback.invoke(status)
}

@Suppress("SpellCheckingInspection")
abstract class BlueberryRequest<ReturnType>
    constructor(

        protected val mReturnTypeClass : Class<ReturnType>,

        protected var mMoshi : Moshi,

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

/*
@Suppress("SpellCheckingInspection")
class BlueberryRequest<ReturnType>
    constructor(internal val mBlueberryDevice: BlueberryDevice<out Any>,
                internal val mRequestType: Class<out Annotation>,
                uuidString : String,
                moshi : Moshi,
                priority : Int,
                inputDataSource : Any?,
                returnTypeClass : Class<ReturnType>) {

    internal val mUuid : UUID = UUID.fromString(uuidString)
    internal var mPriority : Int
    private var mMoshi : Moshi
    private val mInputDataSource : Any?
    private val mReturnTypeClass : Class<ReturnType>
    internal val mInputString : String? by lazy { buildInputString() }

    init {
        mMoshi = moshi
        mPriority = priority
        mInputDataSource = inputDataSource
        mReturnTypeClass = returnTypeClass
        setClearingCallbackQueueOnDisconnected()
    }

    private fun buildInputString() : String? = if(mInputDataSource != null && mRequestType in arrayOf(WRITE::class.java, WRITE_WITHOUT_RESPONSE::class.java))
        if(mInputDataSource::class == String::class || mInputDataSource::class.java.isPrimitive) mInputDataSource.toString() else mMoshi.adapter<Any>(mInputDataSource::class.java).toJson(mInputDataSource)
    else null

    class BlueberryResult<ReturnType>(val status : Int, val data : ReturnType?)
    private val mCallbackQueue : LinkedList<(blueberryResult : BlueberryResult<ReturnType>) -> Unit> = LinkedList()
    private fun setClearingCallbackQueueOnDisconnected() {
        mBlueberryDevice.bluetoothState.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if(mBlueberryDevice.bluetoothState.get() == BlueberryDevice.BluetoothState.STATE_DISCONNECTED) mCallbackQueue.clear()
            }
        })
    }
    fun call(callback : (blueberryResult : BlueberryResult<ReturnType>) -> Unit) {
        mCallbackQueue.offer(callback)
        mBlueberryDevice.enqueueBlueberryRequest(this)
    }
    fun rxCall() : Single<BlueberryResult<ReturnType>> = Single.create<BlueberryResult<ReturnType>> { emitter -> call { blueberryResult -> emitter.onSuccess(blueberryResult) } }
    suspend fun axCall() : BlueberryResult<ReturnType> = suspendCoroutine { continuation -> call { continuation.resume(it) } }

    @Suppress("UNCHECKED_CAST")
    internal fun onResponse(status : Int, dataString : String?) {
        val returnValue : ReturnType? = if(dataString == null) null else when(mRequestType) {
            READ::class.java -> {
                when(mReturnTypeClass) {
                    String::class.java -> dataString as? ReturnType
                    Long::class.java -> dataString.toLong() as? ReturnType
                    Int::class.java -> dataString.toInt() as? ReturnType
                    Short::class.java -> dataString.toShort() as? ReturnType
                    Byte::class.java -> dataString.toByte() as? ReturnType
                    Char::class.java -> dataString[0] as? ReturnType
                    Double::class.java -> dataString.toDouble() as? ReturnType
                    Float::class.java -> dataString.toFloat() as? ReturnType
                    Boolean::class.java -> dataString.toBoolean() as? ReturnType
                    else -> mMoshi.adapter<ReturnType>(mReturnTypeClass).fromJson(dataString)
                }
            }
            else -> null
        }
        mCallbackQueue.poll()?.invoke(BlueberryResult(status, returnValue))
    }

    fun addMoshiAdapters(vararg adapters : Any) {
        this.mMoshi = this.mMoshi.newBuilder().apply {
            adapters.forEach { this.add(it) }
        }.build()
    }

}
 */