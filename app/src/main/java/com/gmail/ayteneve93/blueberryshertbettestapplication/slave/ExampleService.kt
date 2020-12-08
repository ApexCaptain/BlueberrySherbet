package com.gmail.ayteneve93.blueberryshertbettestapplication.slave

import com.gmail.ayteneve93.blueberrysherbetannotations.*
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryNotifyOrIndicateRequestInfo
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryReadRequestInfo
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryWriteRequestInfo
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryWriteRequestInfoWithoutResponse

@BlueberryService
interface ExampleService {


    /* Primitive Service*/
    /* String Characteristic */
    @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
    fun stringRead() : BlueberryReadRequestInfo<String>



    @WRITE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
    fun stringWrite(dataToSend : String) : BlueberryWriteRequestInfo

    @WRITE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101", true)
    fun stringReliableWrite(dataToSend : String) : BlueberryWriteRequestInfo



    @WRITE_WITHOUT_RESPONSE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101", false, "\$EoD")
    fun stringWriteWithoutResponse(dataToSend: String) : BlueberryWriteRequestInfoWithoutResponse

    @WRITE_WITHOUT_RESPONSE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101", true)
    fun stringReliableWriteWithoutResponse(dataToSend: String) : BlueberryWriteRequestInfoWithoutResponse



    @NOTIFY("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101", "\$EoD")
    fun stringNotifyWithEndSignal() : BlueberryNotifyOrIndicateRequestInfo<String>

    @NOTIFY("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
    fun stringNotifyWithoutEndSignal() : BlueberryNotifyOrIndicateRequestInfo<String>



    @INDICATE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101", "\$EoD")
    fun stringIndicateWithEndSignal() : BlueberryNotifyOrIndicateRequestInfo<String>

    @INDICATE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
    fun stringIndicateWithoutEndSignal() : BlueberryNotifyOrIndicateRequestInfo<String>


    /* Gson Data Characteristic */
    @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0102")
    fun simpleDataRead() : BlueberryReadRequestInfo<SimpleData>


    @WRITE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0102")
    fun simpleDataWrite(dataToSend : SimpleData) : BlueberryWriteRequestInfo


}