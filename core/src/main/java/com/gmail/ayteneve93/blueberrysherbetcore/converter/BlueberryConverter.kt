package com.gmail.ayteneve93.blueberrysherbetcore.converter

import java.lang.reflect.ParameterizedType

// https://futurestud.io/tutorials/retrofit-2-introduction-to-multiple-converters
interface BlueberryConverter {

    fun convertToString(objectToConvert : Any) : String
    fun <ObjectType> convertToObject(objectType : Class<ObjectType>, stringToConvert : String) : ObjectType

}