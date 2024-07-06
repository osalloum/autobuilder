package io.github.mattshoe.shoebox.autobuilder.processor.generator

import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ksp.toTypeName
import io.github.mattshoe.shoebox.autobuilder.processor.isNullable

class FunctionCodeGenerator {
    fun generatePropertyMutator(
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

    fun generateBuildFunctionBuilder(packageDestination: String, className: String): FunSpec.Builder {
        return FunSpec.builder("build")
            .returns(ClassName(packageDestination, className))
    }

    fun generateBuildFunction(
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
}