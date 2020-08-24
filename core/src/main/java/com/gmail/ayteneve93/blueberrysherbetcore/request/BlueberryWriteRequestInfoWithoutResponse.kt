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
    private val checkIsReliable : Boolean
    ) : BlueberryAbstractRequestInfo<Any>(
    Any::class.java,
    blueberryDevice,
    priority,
    uuidString
) {
    internal val mInputString : String? by lazy {
        if(inputDataSource == null) null
        else if(inputDataSource::class == String::class || inputDataSource::class.java.isPrimitive) inputDataSource.toString()
        else blueberryConverterPrev.convertObjectToString(inputDataSource)
        //else mMoshi.adapter<Any>(inputDataSource::class.java).toJson(inputDataSource)
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
        checkIsReliable = checkIsReliable
    )

}