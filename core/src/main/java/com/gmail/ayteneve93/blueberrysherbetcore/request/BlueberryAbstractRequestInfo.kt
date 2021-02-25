package com.gmail.ayteneve93.blueberrysherbetcore.request

import com.gmail.ayteneve93.blueberrysherbetcore.converter.BlueberryConverter
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
import com.gmail.ayteneve93.blueberrysherbetcore.request.call.BlueberryAbstractRequest
import java.util.*
import kotlin.collections.HashMap

abstract class BlueberryAbstractRequestInfo<ReturnType>
    constructor(

        internal val mReturnTypeClass : Class<ReturnType>,
        internal val mBlueberryDevice: BlueberryDevice<out Any>,
        internal val mPriority : Int,

        uuidString : String){

    internal val mUuid = UUID.fromString(uuidString)

    internal var blueberryConverter : BlueberryConverter = mBlueberryDevice.blueberryConverter.imitate()

    fun setConverter(blueberryConverter: BlueberryConverter) {
        this.blueberryConverter = blueberryConverter
    }

    protected open fun convertToSimpleHashMap() : HashMap<String, Any?> = HashMap<String, Any?>().apply {
        this["Device MAC Address"] = mBlueberryDevice.mBluetoothDevice.address
        this["Target Device Type"] = mBlueberryDevice::class.java.simpleName
        this["Request Priority"] = mPriority
        this["UUID"] = mUuid
    }


    override fun toString(): String = this::class.java.simpleName + convertToSimpleHashMap().toList().joinToString(
        separator = "\n # ",
        prefix = "\n # ",
        transform = {
            "${it.first} : ${it.second}"
        }
    )

    abstract fun call(awaitingMills : Int = 29000) : BlueberryAbstractRequest

    /*
    companion object {
        @Suppress("UNCHECKED_CAST")
        internal fun <ReturnType>convertStringToObject(returnType : Class<out Any>, dataString : String) : ReturnType? {
            return Gson().fromJson<ReturnType>(dataString, returnType)
        }

        @Suppress("UNCHECKED_CAST")
        internal fun convertObjectToString(dataToConvert : Any) : String {
            return Gson().toJson(dataToConvert)
        }
    }
    */

}