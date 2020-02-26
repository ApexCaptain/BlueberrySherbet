package com.gmail.ayteneve93.blueberryshertbettestapplication.test

import com.squareup.moshi.Json
import java.util.*

data class CertificationInfo(
    @Json(name = "user_id") val userId : String,
    @Json(name = "login_type") val loginType : Int
)

data class SysconfInfo(
    @Json(name = "use_led") val useLed : Boolean,
    @Json(name = "led_brightness") val ledBrightness : Int
)

data class LogData(
    val logTime : Date,
    val logLevel : String,
    val logCode : Int,
    val logMsg : String
)

data class TestNotifyOrIndicateData(
    val test1 : String,
    val test2 : String,
    val test3 : String,
    val test4 : String
)

