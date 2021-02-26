package com.gmail.ayteneve93.converter_gson

import com.gmail.ayteneve93.blueberrysherbetcore.converter.BlueberryConverter
import com.google.gson.Gson

class BlueberryGsonConverter(private val mGson : Gson) : BlueberryConverter {

    override fun <ConversionType> stringify(
        sourceObject: ConversionType,
        conversionClass : Class<ConversionType>): String = mGson.toJson(sourceObject)

    @Suppress("UNCHECKED_CAST")
    override fun <ConversionType> parse(
        sourceString: String,
        conversionClass: Class<ConversionType>
    ): ConversionType? = if(conversionClass == String::class.java) sourceString as ConversionType else mGson.fromJson(sourceString, conversionClass)

    override fun imitate(): BlueberryConverter {
        return BlueberryGsonConverter(mGson.newBuilder().create())
    }

}