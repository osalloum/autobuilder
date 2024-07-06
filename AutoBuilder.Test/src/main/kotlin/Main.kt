package io.github.mattshoe.shoebox.autobuilder

import io.github.mattshoe.shoebox.autobuilder.model.autobuilder.*

fun main() {
    test { AllPrimitivesDefaultedNonNullBuilder().build() }
    test { AllPrimitivesDefaultedNullableBuilder().build() }
    test { AllPrimitivesNonDefaultNonNullBuilder().build() }
    test { AllPrimitivesNonDefaultNullableBuilder().build() }
    test { NonPrimitiveDefaultedWithConstructorBuilder().build() }
    test { NonPrimitiveDefaultedWithConstructorRequiringAdditionalImportBuilder().build() }
    test { NonPrimitiveNonDefaultedWithNoArgConstructorBuilder().build() }
}

fun test(action: () -> Any) {
    println(action())
}

