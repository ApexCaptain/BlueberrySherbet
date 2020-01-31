package com.gmail.ayteneve93.blueberrysherbetcore.request

import com.gmail.ayteneve93.blueberrysherbetannotations.WRITE
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
import com.squareup.moshi.Moshi
import io.reactivex.Single
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Suppress("SpellCheckingInspection")
class BlueberryWriteRequest(
    moshi : Moshi,
    blueberryDevice : BlueberryDevice<out Any>,
    priority : Int,
    uuidString : String,
    inputDataSource : Any?
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

    fun call(awaitingMills : Int = 290000) : BlueberryRequestInfoWithoutResult
            = BlueberryRequestInfoWithoutResult(mUuid, mPriority, awaitingMills, this, WRITE::class.java, mInputString)

}