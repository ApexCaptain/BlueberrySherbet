package com.gmail.ayteneve93.blueberrysherbetcore.request

import com.gmail.ayteneve93.blueberrysherbetannotations.WRITE
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
import com.gmail.ayteneve93.blueberrysherbetcore.request.call.BlueberryRequestWithoutResult
import java.util.HashMap

class BlueberryWriteRequestInfo(
    blueberryDevice : BlueberryDevice<out Any>,
    priority : Int,
    uuidString : String,
    inputDataSource : Any?,
    private val checkIsReliable : Boolean
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

    override fun call(awaitingMills : Int) : BlueberryRequestWithoutResult = BlueberryRequestWithoutResult(
        uuid = mUuid,
        priority = mPriority,
        awaitingMills = awaitingMills,
        blueberryRequestInfo = this,
        requestType = WRITE::class.java,
        inputString = mInputString,
        checkIsReliable = checkIsReliable
    )

}