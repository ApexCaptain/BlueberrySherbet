package com.gmail.ayteneve93.blueberryshertbettestapplication.test

import android.util.Log
import androidx.databinding.Observable
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TestDevice : BlueberryDevice<TestDeviceService>() {

    companion object {
        val name = "NYSLP19020030P"
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

    override fun onRssiValueChanged(rssiValue: Int) {
        super.onRssiValueChanged(rssiValue)
    }

    override fun onPhyValueChanged(txPhy: Int, rxPhy: Int) {
        super.onPhyValueChanged(txPhy, rxPhy)
    }

    override fun onMtuValueChanged(mtu: Int) {
        super.onMtuValueChanged(mtu)
    }

    override fun onServicesDiscovered() {
        super.onServicesDiscovered()

        /*
        val certificationInfo = CertificationInfo(
            "xe6zBDYywHcs16CBTN1Gk2xHZtM2",
            2
        )*/

        GlobalScope.launch { with(blueberryService) {

            /*
            testRead().call().byCoroutine().value?.let {
                Log.d("ayteneve93_test", "rst : $it")
            }
            testRead().call().byCoroutine().value?.let {
                Log.d("ayteneve93_test", "rst : $it")
            }
           testWrtie("Data").call().byCoroutine()
            */

            /*
            repeat(30) {
                testRead().call().byCoroutine().value?.let {
                    Log.d("ayteneve93_test", "rst : $it")
                }
                testWrtie("TestData").call().byCoroutine()
            }
            */
            /*
            val indObj = testIndicate().call().byDataBinding()
            indObj.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                    Log.d("ayteneve93_test", "${indObj.get()}")
                }
            })
            */


            /*
            certificateWithNonReliableWrite(certificationInfo).call().byCoroutine()

            val rst = readCheckWifiStatus().call().byCoroutine()
            rst.value?.let {
                if(it.connectionState) Log.d("ayteneve93_test", it.ip_address)
                else {
                    this.connectWifi(
                        WifiConnectionInfo(
                        ssid = "nayuntech2G",
                        psk = "nyt00630!",
                        timeout = 29000
                    )).call().byCoroutine()
                    val rst2 = readCheckWifiStatus().call().byCoroutine()
                    Log.d("ayteneve93_test", rst2.value?.ip_address?:"Error...")
                }
            }
            Log.d("ayteneve93_test", "Executed...")

             */
        }}

    }
}