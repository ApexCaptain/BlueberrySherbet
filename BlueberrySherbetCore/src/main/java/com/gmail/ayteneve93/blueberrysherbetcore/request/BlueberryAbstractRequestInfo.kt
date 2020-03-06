package com.gmail.ayteneve93.blueberrysherbetcore.request

import android.bluetooth.BluetoothGattCharacteristic
import android.os.Handler
import android.os.Looper
import com.squareup.moshi.Moshi
import java.util.*
import kotlin.collections.HashMap

/*
@Suppress("UNCHECKED_CAST")
private fun <ReturnType>convertStringToObject(returnType : Class<out Any>, dataString : String, moshi: Moshi) : ReturnType? {
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

 */

abstract class BlueberryAbstractRequestInfo(
    internal val uuid : UUID,
    internal val priority : Int,
    internal var awaitingMills : Int,
    internal val blueberryRequest : BlueberryAbstractRequest<out Any>,
    internal val requestType : Class<out Annotation>
) {
    internal val requestCode : Long = requestCount++
    internal var isOnProgress : Boolean = false
    internal val requestTimer = Handler(Looper.getMainLooper())

    protected open fun convertToSimpleHashMap() : HashMap<String, Any?> = HashMap<String, Any?>().apply {
        this["UUID"] = uuid
        this["Priority"] = priority
        this["Awaiting Milliseconds"] = awaitingMills
        this["Request Type"] = requestType.simpleName
        this["Request Code"] = requestCode
        this["Is On Progress"] = isOnProgress
    }

    override fun toString(): String = this::class.java.simpleName + convertToSimpleHashMap().toList().joinToString(
        separator = "\n # ",
        prefix = "\n # ",
        transform = {
            "${it.first} : ${it.second}"
        }
    )

    open fun cancel() = blueberryRequest.mBlueberryDevice.cancelBlueberryRequest(this)
    fun startTimer() {
        requestTimer.postDelayed({
            blueberryRequest.mBlueberryDevice.disconnect()
        }, awaitingMills.toLong())
    }
    internal open fun onResponse(status : Int?, characteristic : BluetoothGattCharacteristic?) {
        requestTimer.removeCallbacksAndMessages(null)
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