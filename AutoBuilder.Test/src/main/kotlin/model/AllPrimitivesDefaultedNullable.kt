package io.github.mattshoe.shoebox.autobuilder.model

import io.github.mattshoe.shoebox.autobuilder.annotations.*

@AutoBuilder
data class AllPrimitivesDefaultedNullable(
    @DefaultInt(42)
    val nullableInt: Int?,
    @DefaultString("default")
    val nullableString: String?,
    @DefaultLong(100L)
    val nullableLong: Long?,
    @DefaultFloat(3.14f)
    val nullableFloat: Float?,
    @DefaultDouble(2.718)
    val nullableDouble: Double?,
    @DefaultBoolean(true)
    val nullableBoolean: Boolean?,
    @DefaultShort(7)
    val nullableShort: Short?,
    @DefaultByte(1)
    val nullableByte: Byte?,
    @DefaultChar('A')
    val nullableChar: Char?
)