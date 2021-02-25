package com.gmail.ayteneve93.blueberrysherbetcore.converter

import org.simpleframework.xml.core.Persister
import java.io.StringWriter

class BlueberrySimpleXmlConverter : BlueberryConverter {
    private val mPersister = Persister()
    override fun <ConversionType> stringify(
        sourceObject: ConversionType,
        conversionClass: Class<ConversionType>
    ) : String =StringWriter().apply {
            mPersister.write(sourceObject, this)
        }.toString()

    override fun <ConversionType> parse(
        sourceString: String,
        conversionClass: Class<ConversionType>
    ): ConversionType? = mPersister.read(conversionClass, sourceString)

    override fun imitate(): BlueberryConverter {
        return this
    }

}