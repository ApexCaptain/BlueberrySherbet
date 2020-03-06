package com.gmail.ayteneve93.blueberrysherbetcore.request

import java.util.*
import kotlin.collections.HashMap

@Suppress("SpellCheckingInspection")
class BlueberryRequestInfoWithNoResponse(
    uuid : UUID,
    priority : Int,
    awaitingMills: Int,
    blueberryRequest : BlueberryAbstractRequest<out Any>,
    requestType : Class<out Annotation>,
    internal val inputString : String?,
    val checkIsReliable: Boolean
) : BlueberryAbstractRequestInfo(uuid, priority, awaitingMills, blueberryRequest, requestType) {
    override fun convertToSimpleHashMap(): HashMap<String, Any?> = super.convertToSimpleHashMap().apply {
        this["Input Data"] = inputString
        this["Use Reliable Write"] = checkIsReliable
    }

    fun enqueue() {
        blueberryRequest.mBlueberryDevice.enqueueBlueberryRequestInfo(this)
    }
}