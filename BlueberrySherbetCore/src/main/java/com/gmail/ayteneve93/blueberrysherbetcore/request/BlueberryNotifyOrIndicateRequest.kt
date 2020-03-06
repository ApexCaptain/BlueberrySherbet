package com.gmail.ayteneve93.blueberrysherbetcore.request

import android.os.Build
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
import com.squareup.moshi.Moshi
import java.util.HashMap

@Suppress("SpellCheckingInspection")
class BlueberryNotifyOrIndicateRequest<ReturnType>(
    returnTypeClass : Class<ReturnType>,
    moshi : Moshi,
    blueberryDevice : BlueberryDevice<out Any>,
    priority : Int,
    uuidString : String,
    private val requestType : Class<out Annotation>,
    private val startString : String,
    private val endString : String
) : BlueberryAbstractRequest<ReturnType>(
    returnTypeClass,
    moshi,
    blueberryDevice,
    priority,
    uuidString) {

    override fun convertToSimpleHashMap(): HashMap<String, Any?> = super.convertToSimpleHashMap().apply {
        this["Return Type"] = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) mReturnTypeClass.typeName
                              else mReturnTypeClass.simpleName
        this["Request Type"] = requestType.simpleName
        this["Start String"] = startString
        this["End String"] = endString
    }

    fun call(awaitingMills : Int = 29000) : BlueberryRequestInfoWithRepetitiousResults<ReturnType>
            = BlueberryRequestInfoWithRepetitiousResults(mUuid, mPriority, awaitingMills, this, requestType, startString, endString)
}