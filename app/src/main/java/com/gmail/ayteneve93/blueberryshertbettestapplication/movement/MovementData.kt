package com.gmail.ayteneve93.blueberryshertbettestapplication.movement

import com.google.gson.*
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

data class WiFiAccessPoint(
    val ssid : String,
    val rssi : Int,
    val encType : EncryptionType
)

enum class EncryptionType {
    @SerializedName("0") OPEN,
    @SerializedName("1") WEP,
    @SerializedName("2") WPA_PSK,
    @SerializedName("3") WPA2_PSK,
    @SerializedName("4") WPA_WPA2_PSK,
    @SerializedName("5") WPA2_ENTERPRISE,
    @SerializedName("6") WIFI_AUTH_MAX;
}

data class WiFiCredential(
    val ssid : String,
    val password : String
)

enum class WiFiConnectionResult {
    @SerializedName("0") SUCCESS,
    @SerializedName("1") WRONG_PASSWORD,
    @SerializedName("2") NO_SSID_AVAILABLE
}

data class WiFiConnectionState (
    val isConnected : Boolean,
    val ssid : String
)
