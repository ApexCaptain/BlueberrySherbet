package com.gmail.ayteneve93.blueberryshertbettestapplication.slave

import android.util.Log
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice

class ExampleDevice : BlueberryDevice<ExampleService>() {

    override fun setServiceImpl(): ExampleService = BlueberryExampleServiceImpl(this)

    override fun onServicesDiscovered() {
        super.onServicesDiscovered()
        Log.d("ayteneve93_test", "discovered")
    }
}