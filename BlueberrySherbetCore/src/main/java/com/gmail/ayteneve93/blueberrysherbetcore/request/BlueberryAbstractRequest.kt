package com.gmail.ayteneve93.blueberrysherbetcore.request

import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
import com.gmail.ayteneve93.blueberrysherbetcore.request.info.BlueberryAbstractRequestInfo
import com.squareup.moshi.Moshi
import java.util.*
import kotlin.collections.HashMap

@Suppress("SpellCheckingInspection")
abstract class BlueberryAbstractRequest<ReturnType>
    constructor(

        internal val mReturnTypeClass : Class<ReturnType>,
        internal var mMoshi : Moshi,
        internal val mBlueberryDevice: BlueberryDevice<out Any>,
        internal val mPriority : Int,

        uuidString : String){

    internal val mUuid = UUID.fromString(uuidString)

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

    abstract fun call(awaitingMills : Int = 29000) : BlueberryAbstractRequestInfo

    fun addMoshiAdapters(vararg adapters : Any) {
        mMoshi = mMoshi.newBuilder().apply {
            adapters.forEach { this.add(it) }
        }.build()
    }

}