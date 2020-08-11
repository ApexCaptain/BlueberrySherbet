package com.gmail.ayteneve93.blueberryshertbettestapplication.test

import com.gmail.ayteneve93.blueberrysherbetannotations.*
import com.gmail.ayteneve93.blueberrysherbetcore.request.*
import com.gmail.ayteneve93.blueberryshertbettestapplication.temp.MyDataClassAsGson
import com.gmail.ayteneve93.blueberryshertbettestapplication.temp.MyEnum
import kotlin.String

@BlueberryService
@Suppress("SpellCheckingInspection")
interface TestDeviceService {

    @WRITE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
    fun registerUserWrite(data : MyDataClassAsGson<MyEnum>) : BlueberryWriteRequest

    /*
    @WRITE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0601")
    fun connectWifi(wifiConnectionInfo: WifiConnectionInfo) : BlueberryWriteRequest

    @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0601")
    fun checkWifiStatus() : BlueberryReadRequest<WifiStatus>
     */

    /*
    @READ("beb5483e-36e1-4688-b7f5-ea07361b26a8")
    fun testRead() : BlueberryReadRequest<String>

    @WRITE("beb5483e-36e1-4688-b7f5-ea07361b26a8", true)
    fun testWrtie(str : String) : BlueberryWriteRequest

    @INDICATE("beb5483e-36e1-4688-b7f5-ea07361b26a8")
    fun testIndicate() : BlueberryNotifyOrIndicateRequest<String>

     */


    /*
    @WRITE("aaaaaaaabbbbccccddddeeeeeeee0101", true)
    fun certificateWithReliableWrite(certificationInfo: CertificationInfo) :  BlueberryWriteRequest

    @WRITE("aaaaaaaabbbbccccddddeeeeeeee0101")
    fun certificateWithNonReliableWrite(certificationInfo: CertificationInfo) : BlueberryWriteRequest

    @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0503")
    fun readSysconfInfo() : BlueberryReadRequest<SysconfInfo>

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
    */

}