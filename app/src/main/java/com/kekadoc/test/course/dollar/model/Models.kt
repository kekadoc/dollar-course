package com.kekadoc.test.course.dollar.model

import kotlinx.serialization.Serializable
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import java.math.BigDecimal

@Serializable
@Root(name = "ValCurs", strict = false)
data class ValCurs @JvmOverloads constructor(
    @field:Attribute(name = "Date")
    var date: String = "",
    @field:Attribute(name = "name")
    var name: String = "",
    @field:ElementList(inline=true, name="Valute")
    var valutes: MutableList<Valute> = mutableListOf()
)

@Serializable
@Root(name = "Valute")
data class Valute @JvmOverloads constructor(
    @field:Attribute(name = "ID")
    var id: String = "",
    @field:Element(name = "NumCode")
    var numCode: Short = 0,
    @field:Element(name = "CharCode")
    var charCode: String = "",
    @field:Element(name = "Nominal")
    var nominal: Int = 0,
    @field:Element(name = "Name")
    var name: String = "",
    @field:Element(name = "Value")
    var value: String = "",
)
val Valute?.numericValue: Double
    get() = this?.value?.replace(",", ".")?.toDoubleOrNull() ?: 0.0

@Serializable
@Root(name = "ValCurs")
data class ValCursRange @JvmOverloads constructor(
    @field:Attribute(name = "ID")
    var id: String = "",
    @field:Attribute(name = "DateRange1")
    var from: String = "",
    @field:Attribute(name = "DateRange2")
    var to: String = "",
    @field:Attribute(name = "name")
    var name: String = "",
    @field:ElementList(inline=true, name="Record")
    var records: MutableList<CourseRecord> = mutableListOf()
)
@Serializable
@Root(name = "Record")
data class CourseRecord @JvmOverloads constructor(
    @field:Attribute(name = "Date")
    var date: String = "",
    @field:Attribute(name = "Id")
    var id: String = "",

    @field:Element(name = "Nominal")
    var nominal: Byte = 0,
    @field:Element(name = "Value")
    var value: String = ""
)