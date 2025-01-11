package io.github.mattshoe.shoebox.autobuilder.processor.generator.function

import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.FunSpec

interface FunctionCodeGenerator {
    fun generatePropertyMutatorFunction(
        property: KSPropertyDeclaration,
        packageDestination: String,
        builderClassName: String
    ): FunSpec

    fun generatePropertyGetterFunction(
        property: KSPropertyDeclaration,
        packageDestination: String,
        builderClassName: String
    ): FunSpec?

    fun generateBuildFunctionBuilder(packageDestination: String, className: String): FunSpec.Builder

    fun generateBuildFunction(
        builder: FunSpec.Builder,
        constructorParams: MutableList<String>,
        packageDestination: String,
        className: String
    ): FunSpec
}