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

    @NOTIFY("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101", "\$EoD")
    fun stringNotifyWithEndSignal() : BlueberryNotifyOrIndicateRequestInfo<String>

    @NOTIFY("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0101")
    fun stringNotifyWithoutEndSignal() : BlueberryNotifyOrIndicateRequestInfo<String>


    /* Integer Characteristic */
    @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0102")
    fun integerRead() : BlueberryReadRequestInfo<Int>

    @WRITE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0102")
    fun integerWrite(dataToSend : Int) : BlueberryWriteRequestInfo

    @WRITE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0102", true)
    fun integerReliableWrite(dataToSend : Int) : BlueberryWriteRequestInfo

    @NOTIFY("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0102", "\$EoD")
    fun integerNotifyWithEndSignal() : BlueberryNotifyOrIndicateRequestInfo<Int>

    @NOTIFY("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0102")
    fun integerNotifyWithoutEndSignal() : BlueberryNotifyOrIndicateRequestInfo<Int>

    /* Object Service */
    /* Json Characteristic */
    @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0201")
    fun gsonRead() : BlueberryReadRequestInfo<Person>

    @WRITE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0201")
    fun gsonWrite(dataToSend: Person) : BlueberryWriteRequestInfo

    @WRITE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0201", true)
    fun gsonReliableWrite(dataToSend : Person) : BlueberryWriteRequestInfo

    @NOTIFY("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0201", "\$EoD")
    fun gsonNotifyWithEndSignal() : BlueberryNotifyOrIndicateRequestInfo<Person>


    /* Moshi Characteristic */
    @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0202")
    fun moshiRead() : BlueberryReadRequestInfo<Animal>


    /* Xml Characteristic */
    @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0203")
    fun simpleXmlRead() : BlueberryReadRequestInfo<Product>

    @WRITE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0203")
    fun simpleXmlWrite(dataToSend : Product) : BlueberryWriteRequestInfo

    @WRITE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0203", true)
    fun simpleXmlReliableWrite(dataToSend : Product) : BlueberryWriteRequestInfo

    @NOTIFY("aaaaaaaa-bbbb-cccc-dddd-eeeeeeee0203", "\$EoD")
    fun simpleXmlNotifyWithEndSignal() : BlueberryNotifyOrIndicateRequestInfo<Product>




}