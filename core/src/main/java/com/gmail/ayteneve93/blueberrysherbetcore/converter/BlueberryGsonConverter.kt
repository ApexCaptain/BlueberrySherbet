package com.gmail.ayteneve93.blueberrysherbetcore.converter

import com.google.gson.Gson

@Suppress("SpellCheckingInspection")
class BlueberryGsonConverter(private val mGson : Gson) : BlueberryConverter{
    override fun convertToString(objectToConvert: Any): String = mGson.toJson(objectToConvert)
    @Suppress("UNCHECKED_CAST")
    override fun <ObjectType> convertToObject(
        objectType: Class<ObjectType>,
        stringToConvert: String
    ): ObjectType {
        return if(objectType == String::class.java) stringToConvert as ObjectType
        else mGson.fromJson(stringToConvert, objectType)
    }
}