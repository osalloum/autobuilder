package io.github.mattshoe.shoebox.autobuilder.model

import io.github.mattshoe.shoebox.autobuilder.annotations.AutoBuilder

@AutoBuilder
data class NonPrimitiveNonDefaultedWithNoArgConstructor(
    val innerObject: NoArgConstructorObject
)