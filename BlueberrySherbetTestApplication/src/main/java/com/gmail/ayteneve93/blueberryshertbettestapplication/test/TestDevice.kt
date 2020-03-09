package com.gmail.ayteneve93.blueberryshertbettestapplication.test

import android.os.Build
import android.util.Log
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class TestDevice : BlueberryDevice<TestDeviceService>() {

    //192.168.0.10
    companion object {
        val name = "NYSLP19020037P"
    }

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

        this.setConnectionPriority(BlueberryConnectionPriority.CONNECTION_PRIORITY_LOW_POWER)
    }

    override fun onDeviceDisconnecting() {
        super.onDeviceDisconnecting()
        Log.d("ayteneve93_test", "test device disconnecting")
    }

    override fun onDeviceDisconnected() {
        super.onDeviceDisconnected()
        Log.d("ayteneve93_test", "test device disconnected")
    }

    override fun onRssiValueChanged(rssiValue: Int) {
        super.onRssiValueChanged(rssiValue)

        Log.d("ayteneve93_test", "rssi : $rssiValue")

    }

    override fun onServicesDiscovered() {
        super.onServicesDiscovered()

        val certificationInfo = CertificationInfo(
            "Uv4OywyiZZhlJDvtm8JCH48WIs03",
            2
        )

        GlobalScope.launch { with(blueberryService) {

            certificateWithNonReliableWrite(certificationInfo).call().byCoroutine()

            testIndicate().call().enqueue { status, value ->
                Log.d("ayteneve93_test", "$value")
            }




        }}

    }
}