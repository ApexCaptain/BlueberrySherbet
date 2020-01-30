package com.gmail.ayteneve93.blueberryshertbettestapplication.test

import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice

class TestDevice : BlueberryDevice<TestDeviceService>() {

    override fun setServiceImpl(): TestDeviceService = BlueberryTestDeviceServiceImpl(this)

}