package com.gmail.ayteneve93.blueberrysherbetcore

import com.gmail.ayteneve93.blueberrysherbetannotations.BlueberryService
import com.gmail.ayteneve93.blueberrysherbetannotations.Priority
import com.gmail.ayteneve93.blueberrysherbetannotations.WRITE
import com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryRequest
import kotlin.String

@BlueberryService
interface TestDeviceService {

    @Priority(3)
    @WRITE("Test uuid String")
    fun test(msg : String?) : BlueberryRequest<Int>?


}