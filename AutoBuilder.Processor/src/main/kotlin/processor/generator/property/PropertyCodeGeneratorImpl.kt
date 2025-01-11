package io.github.mattshoe.shoebox.autobuilder.processor.generator.property

import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toTypeName
import io.github.mattshoe.shoebox.autobuilder.processor.isNullable
import io.github.mattshoe.shoebox.autobuilder.processor.model.PropertyData

class PropertyCodeGeneratorImpl : PropertyCodeGenerator {
    override fun generatePropertyCodeFor(
        property: KSPropertyDeclaration,
        propertyData: PropertyData
    ): PropertySpec {
        val typeName = property.type.toTypeName()

        val initializer = when {
            typeName.isList() -> "mutableListOf()"
            typeName.isMap() -> "mutableMapOf()"
            else -> propertyData.value
        }
        return PropertySpec.Companion.builder(
            property.simpleName.asString(),
            property.type.toTypeName().copy(nullable = property.isNullable)
        )
            .mutable(true)
            .initializer(
                initializer
            )
            .addModifiers(KModifier.PRIVATE)
            .build()
    }

    private fun TypeName.isList(): Boolean {
        return this is ParameterizedTypeName &&
                (rawType.canonicalName.startsWith("kotlin.collections.List")
                        || rawType.canonicalName.startsWith("kotlin.collections.MutableList"))
    }

    private fun TypeName.isMap(): Boolean {
        return this is ParameterizedTypeName &&
                (rawType.canonicalName.startsWith("kotlin.collections.Map")
                        || rawType.canonicalName.startsWith("kotlin.collections.MutableMap"))
    }
}