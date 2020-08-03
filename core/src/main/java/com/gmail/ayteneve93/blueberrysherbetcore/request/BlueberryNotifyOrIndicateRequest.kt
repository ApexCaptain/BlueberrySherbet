package com.gmail.ayteneve93.blueberrysherbetcore.request

import android.os.Build
import androidx.annotation.Keep
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
import com.gmail.ayteneve93.blueberrysherbetcore.request.info.BlueberryRequestInfoWithRepetitiousResults
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
    private val endSignal : String
) : BlueberryAbstractRequest<ReturnType>(
    mReturnTypeClass = returnTypeClass,
    mMoshi = moshi,
    mBlueberryDevice = blueberryDevice,
    mPriority = priority,
    uuidString = uuidString) {

    override fun convertToSimpleHashMap(): HashMap<String, Any?> = super.convertToSimpleHashMap().apply {
        this["Return Type"] = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) mReturnTypeClass.typeName
                              else mReturnTypeClass.simpleName
        this["Request Type"] = requestType.simpleName
        this["End Signal"] = endSignal
    }

    override fun call(awaitingMills : Int) : BlueberryRequestInfoWithRepetitiousResults<ReturnType> = BlueberryRequestInfoWithRepetitiousResults(
        uuid = mUuid,
        priority = mPriority,
        awaitingMills = awaitingMills,
        blueberryRequest = this,
        requestType = requestType,
        endSignal = endSignal
    )
}