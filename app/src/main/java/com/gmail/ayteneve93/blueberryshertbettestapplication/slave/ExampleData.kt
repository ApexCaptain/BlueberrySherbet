package com.gmail.ayteneve93.blueberryshertbettestapplication.slave

import com.google.gson.annotations.SerializedName
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import org.simpleframework.xml.Text

// Data Class For Gson
data class Person(
    val name : String,
    val age : Int
)

// Data Class For Moshi
data class Animal(
    val species : String? = null,
    val name : String? = null
)

// Data Class For Simple XML
@Root(name = "note", strict = true)
data class Product @JvmOverloads constructor(

    @field:Element(name = "name")
    var name: String? = null,

    @field:Element(name = "price")
    var price: Int? = null

)
