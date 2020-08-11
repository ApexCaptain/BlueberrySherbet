package com.gmail.ayteneve93.blueberryshertbettestapplication.temp

import androidx.annotation.Keep
import com.google.gson.*
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type


@Keep data class MyDataClassAsGson<T>(
    @SerializedName("someName") val name : String,
    val other : T
)

@Keep data class MyDataClass2(
    val name : String
)

enum class MyEnum {
    A, B, C;
    /*
    class Serializer : JsonSerializer<MyEnum> {
        override fun serialize(
            src: MyEnum?,
            typeOfSrc: Type?,
            context: JsonSerializationContext?
        ): JsonElement {
            return context!!.serialize(src!!.name)
        }
    }
    class Deserializer : JsonDeserializer<MyEnum> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): MyEnum {
            return fromString(json.toString())
        }
    }
    */
    companion object {
        fun fromString(enumName : String) : MyEnum {
            return values().find { it.name == enumName }!!
        }
    }
}