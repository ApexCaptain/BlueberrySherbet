package com.gmail.ayteneve93.blueberrysherbetcore.scanner

import android.bluetooth.BluetoothDevice
import android.content.Context
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice

class BlueberryScanResult {
    lateinit var bluetoothDevice : BluetoothDevice
    internal constructor(bluetoothDevice : BluetoothDevice) { updateDevice(bluetoothDevice) }
    internal fun updateDevice(bluetoothDevice: BluetoothDevice) { this.bluetoothDevice = bluetoothDevice }
    fun <BlueberryDeviceType : BlueberryDevice<out Any>> connect(context : Context, blueberryDeviceType : Class<BlueberryDeviceType>, autoConnect : Boolean = true) : BlueberryDeviceType  {
        val blueberryDevice = blueberryDeviceType.newInstance()
        blueberryDevice.initialize(bluetoothDevice, context, autoConnect)
        return blueberryDevice
    }

}