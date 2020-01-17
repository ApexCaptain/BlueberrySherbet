package com.gmail.ayteneve93.blueberrysherbet

import com.gmail.ayteneve93.blueberrysherbetannotations.Blueberry
import com.gmail.ayteneve93.blueberrysherbetannotations.WRITE

@Blueberry
interface TestDeviceService {

    @WRITE
    fun test(msg : String) : String

}