package com.gmail.ayteneve93.blueberrysherbetcore.request.info

import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryAbstractRequestInfo
import java.util.*
import kotlin.collections.HashMap

@Suppress("SpellCheckingInspection")
class BlueberryRequestWithNoResponse(
    uuid : UUID,
    priority : Int,
    awaitingMills: Int,
    blueberryRequestInfo : BlueberryAbstractRequestInfo<out Any>,
    requestType : Class<out Annotation>,
    internal val inputString : String?,
    val checkIsReliable: Boolean
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