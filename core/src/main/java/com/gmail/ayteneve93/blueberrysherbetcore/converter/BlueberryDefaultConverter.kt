package com.gmail.ayteneve93.blueberrysherbetcore.converter

class BlueberryDefaultConverter : BlueberryConverter {
    override fun <ConversionType> stringify(
        sourceObject: ConversionType,
        conversionClass: Class<ConversionType>
    ): String = sourceObject.toString()


    @Suppress("UNCHECKED_CAST")
    override fun <ConversionType> parse(
        sourceString: String,
        conversionClass: Class<ConversionType>
    ): ConversionType? = when(conversionClass) {
            String::class.java -> sourceString
            Char::class.java -> sourceString.toCharArray()[0]
            Double::class.java -> sourceString.toDouble()
            Float::class.java -> sourceString.toFloat()
            Long::class.java -> sourceString.toLong()
            Int::class.java -> sourceString.toInt()
            Short::class.java -> sourceString.toShort()
            Byte::class.java -> sourceString.toByte()
            Boolean::class.java -> sourceString.toBoolean()
            else -> throw IllegalArgumentException("${BlueberryConverter::class.java.name} cannot parse string to an object.")
        } as ConversionType


    override fun imitate(): BlueberryConverter = this
}