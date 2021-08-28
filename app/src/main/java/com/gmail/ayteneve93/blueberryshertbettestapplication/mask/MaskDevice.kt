package com.gmail.ayteneve93.blueberryshertbettestapplication.mask

import android.util.Log
import com.gmail.ayteneve93.blueberrysherbetcore.converter.BlueberryConverter
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
import com.gmail.ayteneve93.converter_gson.BlueberryGsonConverter
import com.google.gson.Gson

class MaskDevice : BlueberryDevice<MaskService>() {

    override fun setServiceImpl(): MaskService = BlueberryMaskServiceImpl(this)

    override fun setBlueberryConverter(): BlueberryConverter {
        return BlueberryGsonConverter(
            Gson()
                .newBuilder()
                .create()
        )
    }

    override fun onServicesDiscovered() {
        super.onServicesDiscovered()
        blueberryService.indicateFromRPI().call().enqueue { _, value ->
            Log.d("ayteneve93_test", "$value")
        }
    }

}