package com.gmail.ayteneve93.blueberrysherbetcore.request

import android.os.Build
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
import com.gmail.ayteneve93.blueberrysherbetcore.request.call.BlueberryRequestWithRepetitiousResults
import java.util.HashMap

class BlueberryNotifyOrIndicateRequestInfo<ReturnType>(
    returnTypeClass : Class<ReturnType>,
    blueberryDevice : BlueberryDevice<out Any>,
    priority : Int,
    uuidString : String,
    private val requestType : Class<out Annotation>,
    private val endSignal : String,
    private val useEndSignal : Boolean
) : BlueberryAbstractRequestInfo<ReturnType>(
    mReturnTypeClass = returnTypeClass,
    mBlueberryDevice = blueberryDevice,
    mPriority = priority,
    uuidString = uuidString) {

    override fun convertToSimpleHashMap(): HashMap<String, Any?> = super.convertToSimpleHashMap().apply {
        this["Return Type"] = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) mReturnTypeClass.typeName
                              else mReturnTypeClass.simpleName
        this["Request Type"] = requestType.simpleName
        this["End Signal"] = endSignal
    }

    override fun call(awaitingMills : Int) : BlueberryRequestWithRepetitiousResults<ReturnType> = BlueberryRequestWithRepetitiousResults(
        uuid = mUuid,
        priority = mPriority,
        awaitingMills = awaitingMills,
        blueberryRequestInfo = this,
        requestType = requestType,
        endSignal = endSignal,
        useEndSignal = useEndSignal
    )
}