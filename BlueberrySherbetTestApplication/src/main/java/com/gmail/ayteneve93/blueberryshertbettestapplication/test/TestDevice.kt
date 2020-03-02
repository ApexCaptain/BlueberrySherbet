package com.gmail.ayteneve93.blueberryshertbettestapplication.test

import android.os.Build
import android.util.Log
import androidx.databinding.Observable
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
            val a = certificate(certificationInfo).call().byCoroutine()

            Log.d("ayteneve93_test", "$a")

            val prevWifiStatus = readCheckWifiStatus().call().byCoroutine()

            Log.d("ayteneve93_test", "$prevWifiStatus")

            /*
            prevWifiStatus.value?.let {
                if(!it.connectionState) {
                    connectWifi(WifiConnectionInfo("nayuntech2G","nyt00630!", 20000)).call().byCoroutine()
                    readCheckWifiStatus().call().byCoroutine().let { nextWifiStatus ->
                        Log.d("ayteneve93_test", "$nextWifiStatus")
                    }
                } else Log.d("ayteneve93_test", "$it")
            }

             */



        }}

    }
}
/*
vTOzlo5h8WTMLXqKq9YmRONC3Iz2

I84Na2Gs54cXvNJL5RcPfy3lGEJ3

7qTdtFbxBtPjDY8BEhnzhtOqiiW2

96GJgWoag2g0igPuiXFiT9XJ86p1

XLAtSWZ1QneUUwWtvAVg1aeWHBI3
 */