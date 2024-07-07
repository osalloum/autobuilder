package io.github.mattshoe.shoebox.autobuilder.model

import io.github.mattshoe.shoebox.autobuilder.annotations.AutoBuilder
import io.github.mattshoe.shoebox.autobuilder.annotations.DefaultConstructor

@AutoBuilder
data class NonPrimitiveDefaultedWithConstructorRequiringAdditionalImport(
    @DefaultConstructor(args = ["Foo(42)"], imports = ["io.github.mattshoe.shoebox.autobuilder.model.Foo"])
    val innerObject: RequiredConstructorWhichNeedsImport
)