package com.gmail.ayteneve93.blueberrysherbetcore.device

import com.gmail.ayteneve93.blueberrysherbetcore.utility.BlueberryLogger
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.lang.Exception
import java.lang.reflect.Type

class BlueberryConverterPrev internal constructor() {

    private var mGson = Gson()
    private var mMoshi = Moshi.Builder().build()

    private constructor(gson : Gson, moshi: Moshi) : this() {
        mGson = gson.newBuilder().create()
        mMoshi = moshi.newBuilder().build()
    }

    internal fun imitate() : BlueberryConverterPrev {
        return BlueberryConverterPrev(mGson, mMoshi)
    }

    fun <ConversionType> addGsonAdapter(conversionType : Class<ConversionType>, gsonAdapter : GsonAdapter<ConversionType>) : BlueberryConverterPrev{
        mGson = mGson
            .newBuilder().apply {
                gsonAdapter.getSerializer()?.let { registerTypeAdapter(conversionType, it) }
                gsonAdapter.getDeserializer()?.let { registerTypeAdapter(conversionType, it) }
            }
            .create()
        return this
    }

    class GsonAdapter<ConversionType> {
        private var serialize : ((src : ConversionType?, typeOfSrc : Type?, context : JsonSerializationContext?) -> JsonElement)? = null
        fun setSerializer(serialize : (src : ConversionType?, typeOfSrc : Type?, context : JsonSerializationContext?) -> JsonElement) : GsonAdapter<ConversionType> {
            this.serialize = serialize
            return this
        }
        fun getSerializer() : JsonSerializer<ConversionType>? {
            return if(serialize == null) null
            else JsonSerializer(serialize!!)
        }

        private var deSerialize : ((json : JsonElement?, typeOfConversion : Type?, context : JsonDeserializationContext?) -> ConversionType)? = null
        fun setDeserializer(deSerialize : (json : JsonElement?, typeOfConversion : Type?, context : JsonDeserializationContext?) -> ConversionType) : GsonAdapter<ConversionType>{
            this.deSerialize = deSerialize
            return this
        }
        fun getDeserializer() : JsonDeserializer<ConversionType>? {
            return if(deSerialize == null) null
            else JsonDeserializer(deSerialize!!)
        }
    }

    fun <ConversionType>addMoshiAdapter(type : Class<ConversionType>, jsonAdapter : JsonAdapter<ConversionType>) : BlueberryConverterPrev {
        mMoshi = mMoshi.newBuilder().add(type, jsonAdapter).build()
        return this
    }

    @Suppress("UNCHECKED_CAST")
    fun <ConversionType>convertStringToObject(conversionType : Class<ConversionType>, stringToConvert : String) : ConversionType? {
        if(conversionType == String::class.java) return stringToConvert as ConversionType
        return mGson.fromJson(stringToConvert, conversionType)
    }
    @Suppress("UNCHECKED_CAST")
    fun <ConversionType>convertStringToObject(conversionType : Type, stringToConvert: String) : ConversionType? {
        if(conversionType == object : TypeToken<String>(){}.type) return stringToConvert as ConversionType
        return mGson.fromJson(stringToConvert, conversionType)
    }
    fun convertObjectToString(objectToConvert : Any) : String {
        val exceptions = ArrayList<Exception>()

        try { return mGson.toJson(objectToConvert) }
        catch (exception : Exception) { exceptions.add(exception)}

        try { return mMoshi.adapter<Any>(objectToConvert::class.java).toJson(objectToConvert) }
        catch (exception : Exception) { exceptions.add(exception)}

        exceptions.forEach { BlueberryLogger.e(it.message?:"No Error Message...", it) }
        throw exceptions.last()
    }

}