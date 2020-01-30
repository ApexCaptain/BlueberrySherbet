package com.gmail.ayteneve93.blueberrysherbetcore.request

import android.util.Log
import com.gmail.ayteneve93.blueberrysherbetannotations.READ
import com.gmail.ayteneve93.blueberrysherbetannotations.WRITE
import com.gmail.ayteneve93.blueberrysherbetannotations.WRITE_WITHOUT_RESPONSE
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
import com.squareup.moshi.Moshi
import io.reactivex.Observable
import java.util.*

class BlueberryRequest<ReturnType> {

    internal val mBlueberryDevice : BlueberryDevice<out Any>
    internal val mUuid : UUID
    internal val mRequestType : Class<out Annotation>
    internal var mMoshi : Moshi
    internal var mPriority : Int
    internal val mInputDataSource : Any?
    internal val mReturnTypeClass : Class<ReturnType>

    internal val mInputString : String? by lazy { buildInputString() }

    var isOnProcess = false

    internal val mRequestCode : Int


    constructor(blueberryDevice: BlueberryDevice<out Any>,
                uuidString : String,
                requestType: Class<out Annotation>,
                moshi : Moshi,
                priority : Int,
                inputDataSource : Any?,
                returnTypeClass : Class<ReturnType>) {
        mBlueberryDevice = blueberryDevice
        mUuid = UUID.fromString(uuidString)
        mRequestType = requestType
        mMoshi = moshi
        mPriority = priority
        mInputDataSource = inputDataSource
        mReturnTypeClass = returnTypeClass

        mRequestCode = ++requestCount
    }

    private fun buildInputString() : String? = if(mInputDataSource != null && mRequestType in arrayOf(WRITE::class.java, WRITE_WITHOUT_RESPONSE::class.java))
        if(mInputDataSource::class == String::class || mInputDataSource::class.java.isPrimitive) mInputDataSource.toString() else mMoshi.adapter<Any>(mInputDataSource::class.java).toJson(mInputDataSource)
    else null

    // private
    private lateinit var callback : (status : Int, data : ReturnType?) -> Unit
    fun call(callback : (status : Int, data : ReturnType?) -> Unit) {
        this.callback = callback
        mBlueberryDevice.enqueueBlueberryRequest(this)
    }
    fun rxCall() {
        
    }

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

                    Double::class.java -> dataString.toDouble() as? ReturnType
                    Float::class.java -> dataString.toFloat() as? ReturnType
                    Boolean::class.java -> dataString.toBoolean() as? ReturnType
                    else -> mMoshi.adapter<ReturnType>(mReturnTypeClass).fromJson(dataString)
                }
            }
            else -> null
        }
        callback(status, returnValue)
    }

    fun addMoshiAdapters(vararg adapters : Any) {
        this.mMoshi = this.mMoshi.newBuilder().apply {
            adapters.forEach { this.add(it) }
        }.build()
    }

    companion object {
        private var requestCount = 0
    }

}