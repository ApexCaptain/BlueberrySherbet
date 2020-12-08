package com.gmail.ayteneve93.blueberryshertbettestapplication.slave

import com.gmail.ayteneve93.blueberrysherbetannotations.*
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryNotifyOrIndicateRequestInfo
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryReadRequestInfo
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryWriteRequestInfo
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryWriteRequestInfoWithoutResponse

@BlueberryService
interface ExampleService {


    /* Simple String Characteristic */
    @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
    fun simpleStringRead() : BlueberryReadRequestInfo<String>



    @WRITE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
    fun simpleStringWrite(dataToSend : String) : BlueberryWriteRequestInfo

    @WRITE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101", true)
    fun simpleReliableStringWrite(dataToSend : String) : BlueberryWriteRequestInfo



    @WRITE_WITHOUT_RESPONSE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
    fun simpleStringWriteWithoutResponse(dataToSend: String) : BlueberryWriteRequestInfoWithoutResponse

    @WRITE_WITHOUT_RESPONSE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101", true)
    fun simpleReliableStringWriteWithoutResponse(dataToSend: String) : BlueberryWriteRequestInfoWithoutResponse



    @NOTIFY("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101", "\$EoD")
    fun simpleStringNotifyWithEndSignal() : BlueberryNotifyOrIndicateRequestInfo<String>

    @NOTIFY("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
    fun simpleStringNotifyWithoutEndSignal() : BlueberryNotifyOrIndicateRequestInfo<String>



    @INDICATE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101", "\$EoD")
    fun simpleStringIndicateWithEndSignal() : BlueberryNotifyOrIndicateRequestInfo<String>

    @INDICATE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
    fun simpleStringIndicateWithoutEndSignal() : BlueberryNotifyOrIndicateRequestInfo<String>


    /* Simple Data Characteristic */
    @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0102")
    fun simpleDataRead() : BlueberryReadRequestInfo<SimpleData>

}