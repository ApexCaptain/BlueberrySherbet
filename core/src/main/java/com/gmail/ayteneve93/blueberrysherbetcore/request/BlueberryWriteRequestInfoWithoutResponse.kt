package com.gmail.ayteneve93.blueberrysherbetcore.request

import com.gmail.ayteneve93.blueberrysherbetannotations.WRITE_WITHOUT_RESPONSE
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
import com.gmail.ayteneve93.blueberrysherbetcore.request.call.BlueberryRequestWithNoResponse
import java.util.HashMap

@Suppress("SpellCheckingInspection")
class BlueberryWriteRequestInfoWithoutResponse(
    blueberryDevice : BlueberryDevice<out Any>,
    priority : Int,
    uuidString : String,
    inputDataSource : Any?,
    private val checkIsReliable : Boolean,
    private val endSignal : String
    ) : BlueberryAbstractRequestInfo<Any>(
    Any::class.java,
    blueberryDevice,
    priority,
    uuidString
) {

    @Suppress("UNCHECKED_CAST")
    internal val mInputString : String? by lazy {
        if(inputDataSource == null) null
        else blueberryConverter.stringify(inputDataSource, inputDataSource::class.java as Class<Any>)
    }

    override fun convertToSimpleHashMap(): HashMap<String, Any?> = super.convertToSimpleHashMap().apply {
        this["Input Data"] = mInputString
        this["Use Reliable Write"] = checkIsReliable
    }

    override fun call(awaitingMills : Int) : BlueberryRequestWithNoResponse = BlueberryRequestWithNoResponse(
        uuid = mUuid,
        priority = mPriority,
        awaitingMills = awaitingMills,
        blueberryRequestInfo = this,
        requestType = WRITE_WITHOUT_RESPONSE::class.java,
        inputString = mInputString,
        checkIsReliable = checkIsReliable,
        endSignal = endSignal
    )

}