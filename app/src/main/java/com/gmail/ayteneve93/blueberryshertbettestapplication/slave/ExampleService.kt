package com.gmail.ayteneve93.blueberryshertbettestapplication.slave

import com.gmail.ayteneve93.blueberrysherbetannotations.*
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryNotifyOrIndicateRequestInfo
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryReadRequestInfo
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryWriteRequestInfo
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryWriteRequestInfoWithoutResponse

@BlueberryService
interface ExampleService {

    // Simple String Characteristic
    @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
    fun simpleStringRead() : BlueberryReadRequestInfo<String>

    @WRITE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
    fun simpleStringWrite(dataToSend : String) : BlueberryWriteRequestInfo

    @WRITE_WITHOUT_RESPONSE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
    fun simpleStringWriteWithoutResponse(dataToSend: String) : BlueberryWriteRequestInfoWithoutResponse

    /*
    @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
    fun testRead() : BlueberryReadRequestInfo<ReadData>

    @WRITE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
    fun testWrite(sendingData : WriteData) : BlueberryWriteRequestInfo

    @WRITE_WITHOUT_RESPONSE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
    fun testWriteWithoutResponse(str : String) : BlueberryWriteRequestInfoWithoutResponse

    @NOTIFY("aaaaaaaabbbbccccddddeeeeeeee0101", "\$EoD")
    fun testNotify() : BlueberryNotifyOrIndicateRequestInfo<String>

    @INDICATE("aaaaaaaabbbbccccddddeeeeeeee0101", "\$EoD")
    fun testIndicate() : BlueberryNotifyOrIndicateRequestInfo<String>
    */

}