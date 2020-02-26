package com.gmail.ayteneve93.blueberrysherbetcore.request

import com.gmail.ayteneve93.blueberrysherbetannotations.READ
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
import com.squareup.moshi.Moshi

@Suppress("SpellCheckingInspection")
class BlueberryReadRequest<ReturnType>(
    returnTypeClass : Class<ReturnType>,
    moshi : Moshi,
    blueberryDevice : BlueberryDevice<out Any>,
    priority : Int,
    uuidString : String
    ) : BlueberryRequest<ReturnType>(
    returnTypeClass,
    moshi,
    blueberryDevice,
    priority,
    uuidString){
    fun call(awaitingMills : Int = 29000) : BlueberryRequestInfoWithSimpleResult<ReturnType>
        = BlueberryRequestInfoWithSimpleResult(mUuid, mPriority, awaitingMills, this, READ::class.java)
}