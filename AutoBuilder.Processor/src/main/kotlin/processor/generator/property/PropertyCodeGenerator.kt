package io.github.mattshoe.shoebox.autobuilder.processor.generator.property

import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.PropertySpec
import io.github.mattshoe.shoebox.autobuilder.processor.model.PropertyData

interface PropertyCodeGenerator {
    fun generatePropertyCodeFor(property: KSPropertyDeclaration, propertyData: PropertyData): PropertySpec
}

