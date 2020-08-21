package com.gmail.ayteneve93.blueberrysherbetcore.request

import android.os.Build
import com.gmail.ayteneve93.blueberrysherbetannotations.READ
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
import com.gmail.ayteneve93.blueberrysherbetcore.request.info.BlueberryRequestWithSimpleResult
import java.util.HashMap

@Suppress("SpellCheckingInspection")
class BlueberryReadRequestInfo<ReturnType>(
    returnTypeClass : Class<ReturnType>,
    blueberryDevice : BlueberryDevice<out Any>,
    priority : Int,
    uuidString : String
    ) : BlueberryAbstractRequestInfo<ReturnType>(
    returnTypeClass,
    blueberryDevice,
    priority,
    uuidString){

    override fun convertToSimpleHashMap(): HashMap<String, Any?> = super.convertToSimpleHashMap().apply {
        this["Return Type"] = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) mReturnTypeClass.typeName
                              else mReturnTypeClass.simpleName
    }

    override fun call(awaitingMills : Int) : BlueberryRequestWithSimpleResult<ReturnType> = BlueberryRequestWithSimpleResult(
        uuid = mUuid,
        priority = mPriority,
        awaitingMills = awaitingMills,
        blueberryRequestInfo = this,
        requestType = READ::class.java
    )
}