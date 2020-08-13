package com.gmail.ayteneve93.blueberryshertbettestapplication.slave

import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryDevice

class ExampleDevice : BlueberryDevice<ExampleService>() {

    override fun setServiceImpl(): ExampleService = BlueberryExampleServiceImpl(this)

}