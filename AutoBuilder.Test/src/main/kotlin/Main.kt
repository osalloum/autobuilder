package io.github.mattshoe.shoebox.autobuilder

import io.github.mattshoe.shoebox.autobuilder.annotations.*
import io.github.mattshoe.shoebox.autobuilder.autobuilder.FlerpyDooBuilder


@AutoBuilder
data class FlerpyDoo(
    val int: Int,
    @DefaultLong(543)
    val long: Long,
    @DefaultFloat(9020f)
    val float: Float,
    @DefaultDouble(04545.0)
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
    val flerp = FlerpyDooBuilder().build()
    println(flerp.toString())
}