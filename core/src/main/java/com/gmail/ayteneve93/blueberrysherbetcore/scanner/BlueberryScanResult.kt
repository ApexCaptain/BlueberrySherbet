package com.gmail.ayteneve93.blueberrysherbetcore.scanner

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Build
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
import kotlin.math.pow

class BlueberryScanResult internal constructor(bluetoothDevice: BluetoothDevice){
    init { updateDevice(bluetoothDevice) }
    lateinit var bluetoothDevice : BluetoothDevice
    internal fun updateDevice(bluetoothDevice: BluetoothDevice) { this.bluetoothDevice = bluetoothDevice }
    fun <BlueberryDeviceType : BlueberryDevice<out Any>> interlock(context: Context, blueberryDeviceType: Class<BlueberryDeviceType>, autoConnect: Boolean = true) : BlueberryDeviceType  {
        val blueberryDevice = blueberryDeviceType.newInstance()
        blueberryDevice.initialize(bluetoothDevice, context, autoConnect)
        return blueberryDevice
    }

    val rssi = ObservableField<Int>()

    override fun hashCode(): Int = 31 * bluetoothDevice.address.hashCode()

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other is BlueberryScanResult) return this.bluetoothDevice.address == other.bluetoothDevice.address
        if(other is BluetoothDevice) return this.bluetoothDevice.address == other.address
        return false
    }

    @SuppressLint("MissingPermission")
    override fun toString(): String = HashMap<String, Any?>().apply {
        this@BlueberryScanResult.bluetoothDevice.let {
            put("address", it.address)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) put("alias", it.alias)
            put("name", it.name)
            put("type", when(it.type) {
                BluetoothDevice.DEVICE_TYPE_CLASSIC -> "Classic - BR/EDR devices"
                BluetoothDevice.DEVICE_TYPE_DUAL -> "Dual - BR/EDR/LE"
                BluetoothDevice.DEVICE_TYPE_LE -> "Low Energy - LE-only"
                else -> "Unknown"
            })
            put("bondState", when(it.bondState) {
                BluetoothDevice.BOND_BONDED -> "Bonded (Paired)"
                BluetoothDevice.BOND_BONDING -> "Bonding (Pairing)"
                else -> "Not Bonded (Not Paired)"
            })
            // ToDo : BluetoothClass 정보 추가...?
        }
    }.toList().joinToString(
        separator = "\n # ",
        prefix = "\n # ",
        transform = {
            "${it.first} : ${it.second}"
        }
    )

}