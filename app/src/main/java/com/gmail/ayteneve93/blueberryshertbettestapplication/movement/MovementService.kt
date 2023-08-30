package com.gmail.ayteneve93.blueberryshertbettestapplication.movement

import com.gmail.ayteneve93.blueberrysherbetannotations.*
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryNotifyOrIndicateRequestInfo
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryReadRequestInfo
import com.gmail.ayteneve93.blueberrysherbetcore.request.BlueberryWriteRequestInfo
import com.gmail.ayteneve93.blueberrysherbetcore.request.call.BlueberryRequestWithRepetitiousResults

@BlueberryService
interface MovementService {

    @WRITE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeee101")
    fun setDataSendingState(isEnabled : Boolean) : BlueberryWriteRequestInfo

    @NOTIFY("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeee101")
    fun onDataSendingInitiated() : BlueberryNotifyOrIndicateRequestInfo<String>

    // Wifi
    @WRITE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeee201")
    fun connectToWiFi(wiFiCredential: WiFiCredential) : BlueberryWriteRequestInfo

    @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeee201")
    fun getConnectionResult() : BlueberryReadRequestInfo<WiFiConnectionResult>

    @INDICATE("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeee201", endSignal = "\$EoD")
    fun connectionStatus() : BlueberryNotifyOrIndicateRequestInfo<WiFiConnectionState>

    @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeee202")
    fun scanWiFi() : BlueberryReadRequestInfo<Array<WiFiAccessPoint>>

    @READ("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeee203")
    fun getWiFiStatus() : BlueberryReadRequestInfo<WiFiConnectionState>


}