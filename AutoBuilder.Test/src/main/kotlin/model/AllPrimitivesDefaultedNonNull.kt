package io.github.mattshoe.shoebox.autobuilder.model

import io.github.mattshoe.shoebox.autobuilder.annotations.*

@AutoBuilder
data class AllPrimitivesDefaultedNonNull(
    @DefaultInt(42)
    val int: Int,
    @DefaultLong(42)
    val long: Long,
    @DefaultFloat(42f)
    val float: Float,
    @DefaultDouble(42.0)
    val double: Double,
    @DefaultBoolean(true)
    val boolean: Boolean,
    @DefaultChar('z')
    val char: Char,
    @DefaultByte(42)
    val byte: Byte,
    @DefaultShort(420)
    val short: Short,
    @DefaultString("yay!")
    val string: String,
)