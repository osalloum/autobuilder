package io.github.mattshoe.shoebox.autobuilder.model

import io.github.mattshoe.shoebox.autobuilder.annotations.AutoBuilder
import io.github.mattshoe.shoebox.autobuilder.annotations.Default

@AutoBuilder
data class NonPrimitiveDefaultedWithConstructorRequiringAdditionalImport(
    @Default(args = ["Foo(42)"], imports = ["io.github.mattshoe.shoebox.autobuilder.model.Foo"])
    val innerObject: RequiredConstructorWhichNeedsImport
)