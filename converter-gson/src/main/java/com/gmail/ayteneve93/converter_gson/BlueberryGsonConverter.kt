package com.gmail.ayteneve93.converter_gson

import com.gmail.ayteneve93.blueberrysherbetcore.converter.BlueberryConverter
import com.google.gson.Gson
import java.lang.reflect.Type

class BlueberryGsonConverter(private val mGson : Gson) : BlueberryConverter {

    override fun <ConversionType> stringify(
        sourceObject: ConversionType,
        conversionClass : Class<ConversionType>
    ): String = mGson.toJson(sourceObject)

    fun <ConversionType>  stringify(
        sourceObject : ConversionType,
        conversionType: Type = Any::class.java
    ) : String = mGson.toJson(sourceObject, conversionType)

    @Suppress("UNCHECKED_CAST")
    override fun <ConversionType> parse(
        sourceString: String,
        conversionClass: Class<ConversionType>
    ): ConversionType? = if(conversionClass == String::class.java) sourceString as ConversionType else mGson.fromJson(sourceString, conversionClass)

    fun <ConversionType> parse(
        sourceString: String,
        conversionType: Type
    ) : ConversionType? = mGson.fromJson<ConversionType>(sourceString, conversionType)

    override fun imitate(): BlueberryConverter {
        return BlueberryGsonConverter(mGson.newBuilder().create())
    }

}