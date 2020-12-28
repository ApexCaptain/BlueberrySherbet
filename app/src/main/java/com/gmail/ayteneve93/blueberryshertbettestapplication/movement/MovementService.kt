package com.gmail.ayteneve93.blueberryshertbettestapplication.movement

import com.gmail.ayteneve93.blueberrysherbetannotations.BlueberryService
import com.gmail.ayteneve93.blueberrysherbetannotations.INDICATE
import com.gmail.ayteneve93.blueberrysherbetannotations.READ
import com.gmail.ayteneve93.blueberrysherbetannotations.WRITE
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryNotifyOrIndicateRequestInfo
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryReadRequestInfo
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryWriteRequestInfo
import com.gmail.ayteneve93.blueberrysherbetcore.request.call.BlueberryRequestWithRepetitiousResults

@BlueberryService
interface MovementService {

    @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeee101")
    fun readDeviceInfo() : BlueberryReadRequestInfo<String>


    // Wifi
    @WRITE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeee201")
    fun connectToWiFi(wiFiCredential: WiFiCredential) : BlueberryWriteRequestInfo

    @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeee201")
    fun getConnectionResult() : BlueberryReadRequestInfo<WiFiConnectionResult>

    @INDICATE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeee201", "\$EoD")
    fun connectionStatus() : BlueberryNotifyOrIndicateRequestInfo<WiFiConnectionState>

    @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeee202")
    fun scanWiFi() : BlueberryReadRequestInfo<Array<WiFiAccessPoint>>


}