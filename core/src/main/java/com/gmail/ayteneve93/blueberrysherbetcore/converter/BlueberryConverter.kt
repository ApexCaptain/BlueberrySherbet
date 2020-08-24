package com.gmail.ayteneve93.blueberrysherbetcore.converter

// https://futurestud.io/tutorials/retrofit-2-introduction-to-multiple-converters
interface BlueberryConverter {
    fun <ConversionType>stringify(sourceObject : ConversionType, conversionClass : Class<ConversionType>) : String
    fun <ConversionType>parse(sourceString: String, conversionClass : Class<ConversionType>) : ConversionType?
}