package com.gmail.ayteneve93.converter_moshi

import com.gmail.ayteneve93.blueberrysherbetcore.converter.BlueberryConverter
import com.squareup.moshi.Moshi

class BlueberryMoshiConverter(private val mMoshi : Moshi = Moshi.Builder().build()) : BlueberryConverter {

    override fun <ConversionType> stringify(
        sourceObject: ConversionType,
        conversionClass : Class<ConversionType>): String = mMoshi.adapter(conversionClass).toJson(sourceObject)

    override fun <ConversionType> parse(
        sourceString: String,
        conversionClass: Class<ConversionType>
    ): ConversionType? = mMoshi.adapter(conversionClass).fromJson(sourceString)


    override fun imitate(): BlueberryConverter {
        return BlueberryMoshiConverter(mMoshi.newBuilder().build())
    }


}