package com.gmail.ayteneve93.blueberrysherbetcore.request

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

}