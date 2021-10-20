package com.gmail.ayteneve93.blueberryshertbettestapplication.slave

import android.util.Log
import com.gmail.ayteneve93.blueberrysherbetcore.converter.BlueberryConverter
import com.gmail.ayteneve93.converter_gson.BlueberryGsonConverter
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
import com.google.gson.Gson

class ExampleDevice : BlueberryDevice<ExampleService>() {

    override fun setServiceImpl(): ExampleService = BlueberryExampleServiceImpl(this)

    override fun setBlueberryConverter(): BlueberryConverter {
        return BlueberryGsonConverter(
            Gson()
                .newBuilder()
                .create()
        )
    }

    override fun onServicesDiscovered() {
        super.onServicesDiscovered()
        Log.d("ayteneve93_test", "discovered")
    }
}