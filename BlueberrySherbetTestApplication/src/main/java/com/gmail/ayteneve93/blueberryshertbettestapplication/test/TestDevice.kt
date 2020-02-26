package com.gmail.ayteneve93.blueberryshertbettestapplication.test

import android.util.Log
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TestDevice : BlueberryDevice<TestDeviceService>() {

    override fun setServiceImpl(): TestDeviceService = BlueberryTestDeviceServiceImpl(this).apply {
        addMoshiAdapters(TestDeviceMoshiDateAdapter())
    }

    override fun onDeviceConnecting() {
        super.onDeviceConnecting()
        Log.d("ayteneve93_test", "test device connecting")
    }

    override fun onDeviceConnected() {
        super.onDeviceConnected()
        Log.d("ayteneve93_test", "test device connected")
    }

    override fun onDeviceDisconnecting() {
        super.onDeviceDisconnecting()
        Log.d("ayteneve93_test", "test device disconnecting")
    }

    override fun onDeviceDisconnected() {
        super.onDeviceDisconnected()
        Log.d("ayteneve93_test", "test device disconnected")
    }

    override fun onServicesDiscovered() {
        super.onServicesDiscovered()

        val certificationInfo = CertificationInfo(
            "Uv4OywyiZZhlJDvtm8JCH48WIs03",
            2
        )

        GlobalScope.launch { with(blueberryService) {
            certificate(certificationInfo).call().byCoroutine()
            testIndicate().call().enqueue { status, value ->
                Log.d("ayteneve93_test", "$value")
            }
        }}

    }
}