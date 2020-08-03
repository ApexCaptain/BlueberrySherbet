package com.gmail.ayteneve93.blueberrysherbetcore.scanner

import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.annotation.Keep
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice

@Keep
class BlueberryScanResult internal constructor(bluetoothDevice: BluetoothDevice){
    init { updateDevice(bluetoothDevice) }
    lateinit var bluetoothDevice : BluetoothDevice
    internal fun updateDevice(bluetoothDevice: BluetoothDevice) { this.bluetoothDevice = bluetoothDevice }
    fun <BlueberryDeviceType : BlueberryDevice<out Any>> connect(context : Context, blueberryDeviceType : Class<BlueberryDeviceType>, autoConnect : Boolean = true) : BlueberryDeviceType  {
        val blueberryDevice = blueberryDeviceType.newInstance()
        blueberryDevice.initialize(bluetoothDevice, context, autoConnect)
        return blueberryDevice
    }

}