package com.gmail.ayteneve93.blueberrysherbetcore

import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice

class TestDevice : BlueberryDevice() {

     val mBlueberryService : TestDeviceService = BlueberryTestDeviceServiceImpl(this)

}