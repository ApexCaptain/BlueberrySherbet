package com.gmail.ayteneve93.blueberryshertbettestapplication.test

import android.util.Log
import androidx.databinding.Observable
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryConverter
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice
import com.gmail.ayteneve93.blueberryshertbettestapplication.temp.MyDataClassAsGson
import com.gmail.ayteneve93.blueberryshertbettestapplication.temp.MyEnum
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TestDevice : BlueberryDevice<TestDeviceService>() {

    override fun setServiceImpl(): TestDeviceService = BlueberryTestDeviceServiceImpl(this)

    init {
        blueberryConverter.addGsonAdapter(MyEnum::class.java, BlueberryConverter.GsonAdapter<MyEnum>()
            .setSerializer { src, _, context -> context!!.serialize(src!!.name) }
            .setDeserializer { json, _, _ -> MyEnum.fromString(json.toString()) })
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
        blueberryService.registerUserWrite(MyDataClassAsGson("SangHun", MyEnum.A)).call().enqueue {
            Log.d("ayteneve93_test", "$it")
        }*/


    }
}