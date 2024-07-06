package io.github.mattshoe.shoebox.autobuilder.model

import io.github.mattshoe.shoebox.autobuilder.annotations.AutoBuilder

@AutoBuilder
data class AllPrimitivesNonDefaultNonNull(
    val int: Int,
    val long: Long,
    val float: Float,
    val double: Double,
    val boolean: Boolean,
    val char: Char,
    val byte: Byte,
    val short: Short,
    val string: String,
)