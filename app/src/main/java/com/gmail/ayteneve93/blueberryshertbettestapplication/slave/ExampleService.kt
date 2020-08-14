package com.gmail.ayteneve93.blueberryshertbettestapplication.slave

import com.gmail.ayteneve93.blueberrysherbetannotations.BlueberryService
import com.gmail.ayteneve93.blueberrysherbetannotations.READ
import com.gmail.ayteneve93.blueberrysherbetannotations.WRITE
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryReadRequest
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryWriteRequest

@BlueberryService
interface ExampleService {

    @WRITE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
    fun sayHelloToDevice(data : String) : BlueberryWriteRequest

    @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
    fun beGreetedFromDevice() : BlueberryReadRequest<Int>

    @WRITE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0102")
    fun sayHelloToDevice2(data : String) : BlueberryWriteRequest

}