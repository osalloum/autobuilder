package io.github.mattshoe.shoebox.autobuilder

import io.github.mattshoe.shoebox.autobuilder.model.autobuilder.AllPrimitivesNonDefaultNonNullBuilder
import io.github.mattshoe.shoebox.autobuilder.model.autobuilder.AllPrimitivesNonDefaultNullableBuilder


fun main() {
    test { AllPrimitivesNonDefaultNonNullBuilder().build() }
    test { AllPrimitivesNonDefaultNullableBuilder().build() }
}

fun test(action: () -> Any) {
    println(action())
}