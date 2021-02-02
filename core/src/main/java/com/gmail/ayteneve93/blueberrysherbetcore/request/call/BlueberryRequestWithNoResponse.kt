package com.gmail.ayteneve93.blueberrysherbetcore.request.call

import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryAbstractRequestInfo
import java.util.*
import kotlin.collections.HashMap

class BlueberryRequestWithNoResponse(
    uuid : UUID,
    priority : Int,
    awaitingMills: Int,
    blueberryRequestInfo : BlueberryAbstractRequestInfo<out Any>,
    requestType : Class<out Annotation>,
    internal val inputString : String?,
    val checkIsReliable : Boolean,
    val endSignal : String,
    internal var remainInputString : String? = inputString
) : BlueberryAbstractRequest(
    mUuid =  uuid,
    mPriority = priority,
    mAwaitingMills = awaitingMills,
    mBlueberryRequestInfo = blueberryRequestInfo,
    mRequestType = requestType) {
    override fun convertToSimpleHashMap(): HashMap<String, Any?> = super.convertToSimpleHashMap().apply {
        this["Input Data"] = inputString
        this["Use Reliable Write"] = checkIsReliable
    }

    fun enqueue() {
        mBlueberryRequestInfo.mBlueberryDevice.enqueueBlueberryRequestInfo(this)
    }
}