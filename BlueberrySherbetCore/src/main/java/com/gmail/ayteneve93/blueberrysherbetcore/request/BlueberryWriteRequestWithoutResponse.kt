package com.gmail.ayteneve93.blueberrysherbetcore.request

import com.gmail.ayteneve93.blueberrysherbetannotations.WRITE_WITHOUT_RESPONSE
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
import com.squareup.moshi.Moshi

@Suppress("SpellCheckingInspection")
class BlueberryWriteRequestWithoutResponse(
    moshi : Moshi,
    blueberryDevice : BlueberryDevice<out Any>,
    priority : Int,
    uuidString : String,
    inputDataSource : Any?,
    private val checkIsReliable : Boolean
    ) : BlueberryRequest<Any>(
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

    fun call(awaitingMills : Int = 29000) : BlueberryRequestInfoWithNoResponse
        = BlueberryRequestInfoWithNoResponse(mUuid, mPriority, awaitingMills, this, WRITE_WITHOUT_RESPONSE::class.java, mInputString, checkIsReliable)

}