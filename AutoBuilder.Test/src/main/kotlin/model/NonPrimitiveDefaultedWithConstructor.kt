package io.github.mattshoe.shoebox.autobuilder.model

import io.github.mattshoe.shoebox.autobuilder.annotations.AutoBuilder
import io.github.mattshoe.shoebox.autobuilder.annotations.DefaultConstructor

@AutoBuilder
data class NonPrimitiveDefaultedWithConstructor(
    @DefaultConstructor(args = ["42", "75"])
    val innerObject: RequiredConstructorMultipleArgsObject
)