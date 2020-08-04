package com.gmail.ayteneve93.blueberrysherbetcore.scanner

import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.Context
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
import com.google.gson.Gson

class BlueberryScanResult internal constructor(bluetoothDevice: BluetoothDevice){
    init { updateDevice(bluetoothDevice) }
    lateinit var bluetoothDevice : BluetoothDevice
    internal fun updateDevice(bluetoothDevice: BluetoothDevice) { this.bluetoothDevice = bluetoothDevice }
    fun <BlueberryDeviceType : BlueberryDevice<out Any>> connect(context : Context, blueberryDeviceType : Class<BlueberryDeviceType>, autoConnect : Boolean = true) : BlueberryDeviceType  {
        val blueberryDevice = blueberryDeviceType.newInstance()
        blueberryDevice.initialize(bluetoothDevice, context, autoConnect)
        return blueberryDevice
    }

    override fun equals(other: Any?): Boolean {
        if(other is BlueberryScanResult) return this.bluetoothDevice.address == other.bluetoothDevice.address
        if(other is BluetoothDevice) return this.bluetoothDevice.address == other.address
        return false
    }

    override fun toString(): String = Gson()
        .toJson(HashMap<String, Any?>()
            .apply {
                this@BlueberryScanResult.bluetoothDevice.let {
                    put("address", it.address)
                    put("alias", it.alias)
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
            })
}