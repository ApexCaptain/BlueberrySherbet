package com.gmail.ayteneve93.blueberryshertbettestapplication.slave

import com.google.gson.annotations.SerializedName

data class ReadData(
    @SerializedName("NAME") val name : String,
    @SerializedName("MESSAGE") val message : String
)

data class WriteData(
    @SerializedName("NAME") val name : String,
    @SerializedName("MESSAGE") val message : String
)