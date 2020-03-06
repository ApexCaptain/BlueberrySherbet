package com.gmail.ayteneve93.blueberrysherbetcore.request

import android.os.Build
import com.gmail.ayteneve93.blueberrysherbetannotations.READ
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
import com.squareup.moshi.Moshi
import java.util.HashMap

@Suppress("SpellCheckingInspection")
class BlueberryReadRequest<ReturnType>(
    returnTypeClass : Class<ReturnType>,
    moshi : Moshi,
    blueberryDevice : BlueberryDevice<out Any>,
    priority : Int,
    uuidString : String
    ) : BlueberryAbstractRequest<ReturnType>(
    returnTypeClass,
    moshi,
    blueberryDevice,
    priority,
    uuidString){

    override fun convertToSimpleHashMap(): HashMap<String, Any?> = super.convertToSimpleHashMap().apply {
        this["Return Type"] = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) mReturnTypeClass.typeName
                              else mReturnTypeClass.simpleName
    }

    fun call(awaitingMills : Int = 29000) : BlueberryRequestInfoWithSimpleResult<ReturnType>
        = BlueberryRequestInfoWithSimpleResult(mUuid, mPriority, awaitingMills, this, READ::class.java)
}