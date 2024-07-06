package io.github.mattshoe.shoebox.autobuilder.model

import io.github.mattshoe.shoebox.autobuilder.annotations.AutoBuilder
import io.github.mattshoe.shoebox.autobuilder.annotations.Default

@AutoBuilder
data class NonPrimitiveDefaultedWithConstructor(
    @Default(args = ["42", "75"])
    val innerObject: RequiredConstructorMultipleArgsObject
)