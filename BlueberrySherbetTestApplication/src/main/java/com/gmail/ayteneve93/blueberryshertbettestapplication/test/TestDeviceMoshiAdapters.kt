package com.gmail.ayteneve93.blueberryshertbettestapplication.test

import com.squareup.moshi.*
import java.text.SimpleDateFormat
import java.util.*

@Suppress("SpellCheckingInspection")
class TestDeviceMoshiDateAdapter : JsonAdapter<Date>() {
    private val mDateFormat = SimpleDateFormat(TEST_DEVICE_LOG_DATE_FORMAT, Locale.getDefault())

    @FromJson
    override fun fromJson(reader: JsonReader): Date? =
        try { mDateFormat.parse(reader.nextString()) }
        catch(exception : Exception) { null }

    @ToJson
    override fun toJson(writer: JsonWriter, value: Date?) {
        value?.let { writer.value(value.toString()) }
    }

    companion object {
        private const val TEST_DEVICE_LOG_DATE_FORMAT = "yyyy-HH-dd HH:mm:ss.SSS"
    }

}