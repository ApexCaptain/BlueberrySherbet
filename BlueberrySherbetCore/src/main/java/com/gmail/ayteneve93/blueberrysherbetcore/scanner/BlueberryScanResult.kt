package com.gmail.ayteneve93.blueberrysherbetcore.scanner

import android.bluetooth.BluetoothDevice
import android.content.Context
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice

class BlueberryScanResult {

    lateinit var mBluetoothDevice : BluetoothDevice

    internal constructor(bluetoothDevice : BluetoothDevice) { updateDevice(bluetoothDevice) }
    internal fun updateDevice(bluetoothDevice: BluetoothDevice) { mBluetoothDevice = bluetoothDevice }

    fun <BlueberryDeviceType : BlueberryDevice<Any>> connect(context : Context, autoConnect : Boolean, blueberryDeviceType : Class<BlueberryDeviceType>) : BlueberryDeviceType  {
        val blueberryDevice = blueberryDeviceType.newInstance()
        blueberryDevice.initialize(mBluetoothDevice, context, autoConnect)
        return blueberryDevice
    }

}