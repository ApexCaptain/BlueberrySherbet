package com.gmail.ayteneve93.blueberryshertbettestapplication.test

import com.gmail.ayteneve93.blueberrysherbetannotations.*
import com.gmail.ayteneve93.blueberrysherbetcore.request.*
import kotlin.String

@BlueberryService
interface TestDeviceService {

    @WRITE("aaaaaaaabbbbccccddddeeeeeeee0101", true)
    fun certificateWithReliableWrite(certificationInfo: CertificationInfo) :  BlueberryWriteRequest

    @WRITE("aaaaaaaabbbbccccddddeeeeeeee0101", false)
    fun certificateWithNonReliableWrite(certificationInfo: CertificationInfo) : BlueberryWriteRequest

    @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0503")
    fun readSysconfInfo() : BlueberryReadRequest<SysconfInfo>

    @NOTIFY("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0201","\$EoD")
    fun notifyLog() : BlueberryNotifyOrIndicateRequest<LogData>

    @WRITE_WITHOUT_RESPONSE("aaaaaaaabbbbccccddddeeeeeeee0801")
    fun testWriteWithNoResponse(string: String) : BlueberryWriteRequestWithoutResponse

    @WRITE("aaaaaaaabbbbccccddddeeeeeeee0801")
    fun testWriteWithResponse(string : String) : BlueberryWriteRequest

    @NOTIFY("aaaaaaaabbbbccccddddeeeeeeee0801", "\$EoD")
    fun testNotify() : BlueberryNotifyOrIndicateRequest<TestNotifyOrIndicateData>

    @INDICATE("aaaaaaaabbbbccccddddeeeeeeee0801", "\$EoD")
    fun testIndicate() : BlueberryNotifyOrIndicateRequest<TestNotifyOrIndicateData>

    @WRITE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0602")
    fun connectWifi(wifiConnectionInfo: WifiConnectionInfo) : BlueberryWriteRequest

    @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0601")
    fun readCheckWifiStatus() : BlueberryReadRequest<WifiStatus>

}