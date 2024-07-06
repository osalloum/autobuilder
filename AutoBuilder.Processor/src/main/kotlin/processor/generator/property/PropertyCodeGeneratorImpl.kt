package io.github.mattshoe.shoebox.autobuilder.processor.generator.property

import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.ksp.toTypeName
import io.github.mattshoe.shoebox.autobuilder.processor.isNullable
import io.github.mattshoe.shoebox.autobuilder.processor.model.PropertyData

class PropertyCodeGeneratorImpl : PropertyCodeGenerator {
    override fun generatePropertyCodeFor(property: KSPropertyDeclaration, propertyData: PropertyData): PropertySpec {
        return PropertySpec.Companion.builder(
            property.simpleName.asString(),
            property.type.toTypeName().copy(nullable = property.isNullable)
        )
            .mutable(true)
            .initializer(
                propertyData.value
            )
            .addModifiers(KModifier.PRIVATE)
            .build()
    }
}