package com.gmail.ayteneve93.blueberryshertbettestapplication.slave

import android.util.Log
import com.gmail.ayteneve93.blueberrysherbetcore.converter.BlueberryConverter
import com.gmail.ayteneve93.converter_gson.BlueberryGsonConverter
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
import com.google.gson.Gson

class ExampleDevice : BlueberryDevice<ExampleService>() {

    override fun setServiceImpl(): ExampleService = BlueberryExampleServiceImpl(this)

    override fun onDeviceConnected() {
        super.onDeviceConnected()
        Log.d("ayteneve93_test", "connected")
    }

    override fun onDeviceDisconnected() {
        super.onDeviceDisconnected()
        Log.d("ayteneve93_test", "disconnected")
    }

    override fun onServicesDiscovered() {
        super.onServicesDiscovered()
        Log.d("ayteneve93_test", "service discovered")
    }
}