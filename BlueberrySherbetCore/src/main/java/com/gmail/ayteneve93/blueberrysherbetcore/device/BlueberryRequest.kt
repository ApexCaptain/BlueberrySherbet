package com.gmail.ayteneve93.blueberrysherbetcore.device

import com.gmail.ayteneve93.blueberrysherbetannotations.BlueberryRequestType
import java.util.*

class BlueberryRequest<ReturnType> {

    internal val mBlueberryDevice : BlueberryDevice
    internal val mUuid : UUID
    internal val mRequestType : BlueberryRequestType
    internal val mRequestCode : Int

    internal var mPriority : Int

    constructor(blueberryDevice: BlueberryDevice, uuidString : String, requestType: BlueberryRequestType, priority : Int = 10) {
        mBlueberryDevice = blueberryDevice
        mUuid = UUID.fromString(uuidString)
        mRequestType = requestType
        mRequestCode = ++requestCount
        mPriority = priority
    }

    private fun enqeueRequest() {
    }

    companion object {
        private var requestCount = 0
    }

}