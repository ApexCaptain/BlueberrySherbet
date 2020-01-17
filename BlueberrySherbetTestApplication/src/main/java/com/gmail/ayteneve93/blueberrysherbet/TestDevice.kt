package com.gmail.ayteneve93.blueberrysherbet

import com.gmail.ayteneve93.blueberrysherbet.device.BlueberryDevice

class TestDevice : BlueberryDevice() {

    val mBlueberryService = BlueberryTestDeviceServiceImpl(this)

}