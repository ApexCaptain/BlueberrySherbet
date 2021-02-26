package com.gmail.ayteneve93.blueberryshertbettestapplication.slave

import android.util.Log
import com.gmail.ayteneve93.blueberrysherbetcore.converter.BlueberryConverter
import com.gmail.ayteneve93.converter_gson.BlueberryGsonConverter
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
import com.gmail.ayteneve93.converter_moshi.BlueberryMoshiConverter
import com.google.gson.Gson
import com.squareup.moshi.Moshi

class ExampleDevice : BlueberryDevice<ExampleService>() {

    override fun setServiceImpl(): ExampleService = BlueberryExampleServiceImpl(this)

    override fun setBlueberryConverter(): BlueberryConverter {
        return BlueberryMoshiConverter(Moshi.Builder().build())
    }

    override fun onServicesDiscovered() {
        super.onServicesDiscovered()
        Log.d("ayteneve93_test", "discovered")
    }
}