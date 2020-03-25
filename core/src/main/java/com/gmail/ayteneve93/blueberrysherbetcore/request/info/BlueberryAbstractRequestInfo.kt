package com.gmail.ayteneve93.blueberrysherbetcore.request.info

import android.bluetooth.BluetoothGattCharacteristic
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryAbstractRequest
import com.squareup.moshi.Moshi
import java.util.*
import kotlin.collections.HashMap

abstract class BlueberryAbstractRequestInfo (
    internal val mUuid : UUID,
    internal val mPriority : Int,
    internal var mAwaitingMills : Int,
    internal val mBlueberryRequest : BlueberryAbstractRequest<out Any>,
    internal val mRequestType : Class<out Annotation>
) : Comparable<BlueberryAbstractRequestInfo> {
    internal val mRequestCode : Long = requestCount++
    internal var mIsOnProgress : Boolean = false
    internal val mRequestTimer = Handler(Looper.getMainLooper())

    protected open fun convertToSimpleHashMap() : HashMap<String, Any?> = HashMap<String, Any?>().apply {
        this["UUID"] = mUuid
        this["Priority"] = mPriority
        this["Awaiting Milliseconds"] = mAwaitingMills
        this["Request Type"] = mRequestType.simpleName
        this["Request Code"] = mRequestCode
        this["Is On Progress"] = mIsOnProgress
    }

    override fun compareTo(other: BlueberryAbstractRequestInfo): Int = this.mPriority.compareTo(other.mPriority)

    override fun toString(): String = this::class.java.simpleName + convertToSimpleHashMap().toList().joinToString(
        separator = "\n # ",
        prefix = "\n # ",
        transform = {
            "${it.first} : ${it.second}"
        }
    )

    internal fun startTimer() {
        mRequestTimer.postDelayed({
            mBlueberryRequest.mBlueberryDevice.disconnect()
        }, mAwaitingMills.toLong())
    }

    open fun cancel() = mBlueberryRequest.mBlueberryDevice.cancelBlueberryRequest(this)
    internal open fun onResponse(status : Int?, characteristic : BluetoothGattCharacteristic?) {
        mRequestTimer.removeCallbacksAndMessages(null)
    }

    companion object {
        private var requestCount : Long = 0L
        @Suppress("UNCHECKED_CAST")
        internal fun <ReturnType>convertStringToObject(returnType : Class<out Any>, dataString : String, moshi : Moshi) : ReturnType? = when(returnType) {
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

}