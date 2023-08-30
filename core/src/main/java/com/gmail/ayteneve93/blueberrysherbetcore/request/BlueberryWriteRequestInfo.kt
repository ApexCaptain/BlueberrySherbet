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
    private val checkIsReliable : Boolean,
    private val useSimpleBytes : Boolean
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

    @Suppress("UNCHECKED_CAST")
    internal val mInputBytes : ByteArray ? by lazy {
        when {
            inputDataSource is ByteArray -> inputDataSource
            inputDataSource is Array<*> && inputDataSource.isArrayOf<Byte>() -> (inputDataSource as Array<Byte>).toByteArray()
            inputDataSource is ArrayList<*> && inputDataSource.toArray().isArrayOf<Byte>() -> (inputDataSource as ArrayList<Byte>).toByteArray()
            else -> null
        }
    }

    override fun convertToSimpleHashMap(): HashMap<String, Any?> = super.convertToSimpleHashMap().apply {
        this["Input Data"] = if(useSimpleBytes) mInputBytes else mInputString
        this["Use Reliable Write"] = checkIsReliable
    }

    override fun call(awaitingMills : Int) : BlueberryRequestWithoutResult = BlueberryRequestWithoutResult(
        uuid = mUuid,
        priority = mPriority,
        awaitingMills = awaitingMills,
        blueberryRequestInfo = this,
        requestType = WRITE::class.java,
        inputString = mInputString,
        inputBytes = mInputBytes,
        checkIsReliable = checkIsReliable,
        useSimpleBytes = useSimpleBytes
    )

}