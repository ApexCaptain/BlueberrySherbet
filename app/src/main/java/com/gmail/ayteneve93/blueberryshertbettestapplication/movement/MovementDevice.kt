package com.gmail.ayteneve93.blueberryshertbettestapplication.movement

import android.util.Log
import com.gmail.ayteneve93.blueberrysherbetcore.converter.BlueberryConverter
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
import com.gmail.ayteneve93.converter_gson.BlueberryGsonConverter
import com.google.gson.Gson

class MovementDevice : BlueberryDevice<MovementService>() {

    override fun setServiceImpl(): MovementService = BlueberryMovementServiceImpl(this)

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