package com.gmail.ayteneve93.blueberrysherbetcore.request

import com.gmail.ayteneve93.blueberrysherbetannotations.NOTIFY
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
import com.squareup.moshi.Moshi

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
) : BlueberryRequest<ReturnType>(
    returnTypeClass,
    moshi,
    blueberryDevice,
    priority,
    uuidString) {
    fun call(awaitingMills : Int = 29000) : BlueberryRequestInfoWithRepetitiousResults<ReturnType>
            = BlueberryRequestInfoWithRepetitiousResults(mUuid, mPriority, awaitingMills, this, requestType, startString, endString)
}