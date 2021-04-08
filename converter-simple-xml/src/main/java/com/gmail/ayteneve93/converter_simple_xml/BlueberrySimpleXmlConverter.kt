package com.gmail.ayteneve93.converter_simple_xml

import android.util.Log
import com.gmail.ayteneve93.blueberrysherbetcore.converter.BlueberryConverter
import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.filter.Filter
import org.simpleframework.xml.filter.PlatformFilter
import org.simpleframework.xml.strategy.Strategy
import org.simpleframework.xml.strategy.TreeStrategy
import org.simpleframework.xml.stream.Format
import org.simpleframework.xml.transform.Matcher
import org.simpleframework.xml.transform.Transform
import java.io.StringWriter

class BlueberrySimpleXmlConverter : BlueberryConverter {
    private var mPersister : Persister = Persister()
    private var mStrategy : Strategy? = null
    private var mFilter : Filter? = null
    private var mMatcher : Matcher? = null
    private var mFormat : Format? = null

    override fun <ConversionType> stringify(
        sourceObject: ConversionType,
        conversionClass: Class<ConversionType>
    ) : String = StringWriter().apply {
            mPersister.write(sourceObject, this)
        }.toString()

    override fun <ConversionType> parse(
        sourceString: String,
        conversionClass: Class<ConversionType>
    ): ConversionType? = mPersister.read(conversionClass, sourceString)

    override fun imitate(): BlueberryConverter {
        return BlueberrySimpleXmlConverter()
            .setPersister(
                mStrategy,
                mFilter,
                mMatcher,
                mFormat
            )
    }

    fun setPersister(
        strategy: Strategy? = null,
        filter: Filter? = null,
        matcher: Matcher? = null,
        format: Format? = null
    ) : BlueberrySimpleXmlConverter {
        if(strategy != null) mStrategy = strategy
        if(filter != null) mFilter = filter
        if(matcher != null) mMatcher = matcher
        if(format != null) mFormat = format
        mPersister = Persister(
            mStrategy,
            mFilter,
            mMatcher,
            mFormat
        )
        return this
    }

}