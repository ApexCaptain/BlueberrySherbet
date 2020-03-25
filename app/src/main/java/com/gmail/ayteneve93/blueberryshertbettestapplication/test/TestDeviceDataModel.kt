package com.gmail.ayteneve93.blueberryshertbettestapplication.test

import androidx.annotation.Keep
import com.squareup.moshi.Json
import java.util.*

@Keep data class CertificationInfo(
    @Json(name = "user_id") val userId : String,
    @Json(name = "login_type") val loginType : Int
)

@Keep data class SysconfInfo(
    @Json(name = "use_led") val useLed : Boolean,
    @Json(name = "led_brightness") val ledBrightness : Int
)

@Keep data class LogData(
    val logTime : Date,
    val logLevel : String,
    val logCode : Int,
    val logMsg : String
)

@Keep data class TestNotifyOrIndicateData(
    val test1 : String,
    val test2 : String,
    val test3 : String,
    val test4 : String
)

@Suppress("SpellCheckingInspection")
@Keep data class WifiStatus(
    val connectionState : Boolean,
    val ssid : String,
    val ip_address : String
)

@Suppress("SpellCheckingInspection")
@Keep data class WifiConnectionInfo(
    val ssid : String,
    val psk : String,
    val timeout : Int
)

