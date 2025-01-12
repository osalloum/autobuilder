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
        val isNullable = property.isNullable

        // If property has an explicit null value in PropertyData, respect that
        if (isNullable && propertyData.value == "null") {
            return PropertySpec.Companion.builder(
                property.simpleName.asString(),
                typeName.copy(nullable = true)
            )
                .mutable(true)
                .initializer("null")
                .addModifiers(KModifier.PRIVATE)
                .build()
        }

        val initializer = when {
            typeName.isList() -> "mutableListOf()"
            typeName.isMap() -> "mutableMapOf()"
            typeName.isInstant() -> "Instant.now()"
            typeName.isLocalDate() -> "LocalDate.now()"
            typeName.isLocalDateTime() -> "LocalDateTime.now()"
            else -> propertyData.value
        }

        return PropertySpec.Companion.builder(
            property.simpleName.asString(),
            typeName.copy(nullable = isNullable)
        )
            .mutable(true)
            .initializer(initializer)
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

    private fun TypeName.isInstant(): Boolean {
        return this.toString() == "java.time.Instant"
    }

    private fun TypeName.isLocalDate(): Boolean {
        return this.toString() == "java.time.LocalDate"
    }

    private fun TypeName.isLocalDateTime(): Boolean {
        return this.toString() == "java.time.LocalDateTime"
    }
}