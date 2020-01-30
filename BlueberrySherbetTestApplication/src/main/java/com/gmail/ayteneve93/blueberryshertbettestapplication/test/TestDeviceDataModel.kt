package com.gmail.ayteneve93.blueberryshertbettestapplication.test

import com.squareup.moshi.Json

data class CertificationInfo(
    @Json(name = "user_id") val userId : String,
    @Json(name = "login_type") val loginType : Int
)

data class SysconfInfo(
    @Json(name = "use_led") val useLed : Boolean,
    @Json(name = "led_brightness") val ledBrightness : Int
)