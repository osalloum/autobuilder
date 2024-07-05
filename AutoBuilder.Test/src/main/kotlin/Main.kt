package io.github.mattshoe.shoebox.autobuilder

import io.github.mattshoe.shoebox.autobuilder.annotations.*
import io.github.mattshoe.shoebox.autobuilder.autobuilder.FlerpyDooBuilder


@AutoBuilder
data class FlerpyDoo(
    @DefaultInt(45)
    val int: Int,
    @DefaultLong(0)
    val long: Long,
    @DefaultFloat(0f)
    val float: Float,
    @DefaultDouble(0.0)
    val double: Double,
    @DefaultBoolean(true)
    val boolean: Boolean,
    @DefaultChar('w')
    val char: Char,
    @DefaultByte(96)
    val byte: Byte,
    @DefaultShort(10)
    val short: Short,
    @DefaultString("wow!")
    val string: String,
)



fun main() {
    println(FlerpyDooBuilder().build().toString())
}