package io.github.mattshoe.shoebox.autobuilder.processor.generator.function

import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ksp.toTypeName
import io.github.mattshoe.shoebox.autobuilder.processor.isNullable

class FunctionCodeGeneratorImpl : FunctionCodeGenerator {

    override fun generatePropertyMutatorFunction(
        property: KSPropertyDeclaration,
        packageDestination: String,
        builderClassName: String
    ): FunSpec {
        return FunSpec.builder(property.simpleName.asString())
            .addParameter(property.simpleName.asString(), property.type.toTypeName().copy(nullable = property.isNullable))
            .returns(ClassName(packageDestination, builderClassName))
            .addStatement("this.${property.simpleName.asString()}·=·${property.simpleName.asString()}")
            .addStatement("return·this")
            .build()
    }

    override fun generatePropertyGetterFunction(
        property: KSPropertyDeclaration,
        packageDestination: String,
        builderClassName: String
    ): FunSpec? {
        val propertyType = property.type.resolve()
        if (propertyType.isListType() || propertyType.isMapType()) {
            return FunSpec.builder(property.simpleName.asString())
                .returns(property.type.toTypeName())
                .addStatement("return·this.${property.simpleName.asString()}")
                .build()
        }
        return null
    }

    override fun generateBuildFunctionBuilder(packageDestination: String, className: String): FunSpec.Builder {
        return FunSpec.builder("build")
            .returns(ClassName(packageDestination, className))
    }

    override fun generateBuildFunction(
        builder: FunSpec.Builder,
        constructorParams: MutableList<String>,
        packageDestination: String,
        className: String
    ): FunSpec {
        return builder
            .addStatement(
                "return %T(\n    ${constructorParams.joinToString(",\n    ")}\n)",
                ClassName(packageDestination, className)
            )
            .build()
    }

    private fun KSType.isListType(): Boolean {
        val declaration = this.declaration
        return declaration.qualifiedName?.asString() == "kotlin.collections.List" ||
                declaration.qualifiedName?.asString() == "kotlin.collections.MutableList"
    }

    private fun KSType.isMapType(): Boolean {
        val declaration = this.declaration
        return declaration.qualifiedName?.asString() == "kotlin.collections.Map" ||
                declaration.qualifiedName?.asString() == "kotlin.collections.MutableMap"
    }
}