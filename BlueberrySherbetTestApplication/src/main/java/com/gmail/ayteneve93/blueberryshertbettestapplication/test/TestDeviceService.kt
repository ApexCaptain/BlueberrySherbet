package com.gmail.ayteneve93.blueberryshertbettestapplication.test

import com.gmail.ayteneve93.blueberrysherbetannotations.*
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryRequest
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryWriteRequest
import kotlin.String

@BlueberryService
interface TestDeviceService {
    @WRITE("aaaaaaaabbbbccccddddeeeeeeee0101")
    fun certificate(certificationInfo: CertificationInfo) :  BlueberryWriteRequest

    /*
    @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0503")
    fun readSysconfInfo() : BlueberryRequest<SysconfInfo>
     */

}