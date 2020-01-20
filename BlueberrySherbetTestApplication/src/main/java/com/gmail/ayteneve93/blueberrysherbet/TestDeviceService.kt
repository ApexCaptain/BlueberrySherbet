package com.gmail.ayteneve93.blueberrysherbet

import com.gmail.ayteneve93.blueberrysherbetannotations.BlueberryService
import com.gmail.ayteneve93.blueberrysherbetannotations.WRITE

@BlueberryService
interface TestDeviceService {

    @WRITE
    fun test(msg : String) : String


}