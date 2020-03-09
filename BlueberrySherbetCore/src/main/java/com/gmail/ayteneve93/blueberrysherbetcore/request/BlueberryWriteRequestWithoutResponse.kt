package com.gmail.ayteneve93.blueberrysherbetcore.request

import com.gmail.ayteneve93.blueberrysherbetannotations.WRITE_WITHOUT_RESPONSE
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
import com.gmail.ayteneve93.blueberrysherbetcore.request.info.BlueberryRequestInfoWithNoResponse
import com.squareup.moshi.Moshi
import java.util.HashMap

@Suppress("SpellCheckingInspection")
class BlueberryWriteRequestWithoutResponse(
    moshi : Moshi,
    blueberryDevice : BlueberryDevice<out Any>,
    priority : Int,
    uuidString : String,
    inputDataSource : Any?,
    private val checkIsReliable : Boolean
    ) : BlueberryAbstractRequest<Any>(
    Any::class.java,
    moshi,
    blueberryDevice,
    priority,
    uuidString
) {
    internal val mInputString : String? by lazy {
        if(inputDataSource == null) null
        else if(inputDataSource::class == String::class || inputDataSource::class.java.isPrimitive) inputDataSource.toString()
        else mMoshi.adapter<Any>(inputDataSource::class.java).toJson(inputDataSource)
    }

    override fun convertToSimpleHashMap(): HashMap<String, Any?> = super.convertToSimpleHashMap().apply {
        this["Input Data"] = mInputString
        this["Use Reliable Write"] = checkIsReliable
    }

    override fun call(awaitingMills : Int) : BlueberryRequestInfoWithNoResponse = BlueberryRequestInfoWithNoResponse(
        uuid = mUuid,
        priority = mPriority,
        awaitingMills = awaitingMills,
        blueberryRequest = this,
        requestType = WRITE_WITHOUT_RESPONSE::class.java,
        inputString = mInputString,
        checkIsReliable = checkIsReliable
    )

}