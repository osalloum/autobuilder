package io.github.mattshoe.shoebox.autobuilder.processor.generator

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import io.github.mattshoe.shoebox.autobuilder.processor.defaults.DefaultProvider
import io.github.mattshoe.shoebox.autobuilder.processor.isNullable
import io.github.mattshoe.shoebox.autobuilder.processor.model.GeneratedBuilderFile
import io.github.mattshoe.shoebox.autobuilder.processor.model.ImportStatement

class BuilderClassCodeGenerator(
    private val defaultProvider: DefaultProvider,
    private val propertyCodeGenerator: PropertyCodeGenerator,
    private val functionCodeGenerator: FunctionCodeGenerator
) {
    fun generateBuilderCodeFor(classDeclaration: KSClassDeclaration, resolver: Resolver): GeneratedBuilderFile {
        val packageDestination = packageDestination(classDeclaration)
        val classBeingBuilt = classDeclaration.simpleName.asString()
        val builderClassName = "${classBeingBuilt}Builder"
        val builderImports = mutableSetOf<ImportStatement>()

        val builderClass = generateBuilderClass(
            builderClassName,
            packageDestination,
            classBeingBuilt,
            classDeclaration,
            resolver,
            builderImports
        )

        return GeneratedBuilderFile(
            packageDestination,
            classBeingBuilt,
            builderClassName,
            generateFileSpec(
                classDeclaration,
                packageDestination,
                classBeingBuilt,
                builderClassName,
                builderClass,
                builderImports
            )
        )
    }

    private fun generateBuilderClass(
        builderClassName: String,
        packageDestination: String,
        classBeingBuilt: String,
        classDeclaration: KSClassDeclaration,
        resolver: Resolver,
        builderImports: MutableSet<ImportStatement>
    ): TypeSpec {
        return TypeSpec.classBuilder(builderClassName)
            .addModifiers(KModifier.PUBLIC)
            .apply {
                val buildFunction = functionCodeGenerator.generateBuildFunctionBuilder(packageDestination, classBeingBuilt)
                val constructorParams = mutableListOf<String>()

                classDeclaration.getAllProperties().forEach { property ->
                    generateMutators(
                        property,
                        resolver,
                        builderImports,
                        packageDestination,
                        builderClassName
                    )
                    updateCosntructorParams(property, constructorParams)
                }

                generateBuildFunction(buildFunction, constructorParams, packageDestination, classBeingBuilt)
            }
            .build()
    }

    private fun generateFileSpec(
        classDeclaration: KSClassDeclaration,
        packageDestination: String,
        classBeingBuilt: String,
        builderClassName: String,
        builderClass: TypeSpec,
        builderImports: MutableSet<ImportStatement>
    ): FileSpec {
        return FileSpec.builder(packageDestination, builderClassName).apply {
            indent("    ")
            addType(builderClass)
            addImport(packageName = classDeclaration.packageName.asString(), classBeingBuilt)
            builderImports.forEach {
                addImport(
                    packageName = it.packageName,
                    it.className
                )
            }
        }.build()
    }

    private fun TypeSpec.Builder.generateMutators(
        property: KSPropertyDeclaration,
        resolver: Resolver,
        builderImports: MutableSet<ImportStatement>,
        packageDestination: String,
        builderClassName: String
    ) {
        addBuilderProperty(property,resolver, builderImports)
        addBuilderFunction(property, packageDestination, builderClassName)
    }

    private fun TypeSpec.Builder.addBuilderProperty(
        property: KSPropertyDeclaration,
        resolver: Resolver,
        builderImports: MutableSet<ImportStatement>
    ) {
        with (defaultProvider.defaultPropertyValue(property, resolver)) {
            builderImports.addAll(this.imports)
            addProperty(
                propertyCodeGenerator.generatePropertyCodeFor(property, this)
            )
        }
    }

    private fun TypeSpec.Builder.addBuilderFunction(
        property: KSPropertyDeclaration,
        packageDestination: String,
        builderClassName: String
    ) {
        addFunction(
            functionCodeGenerator.generatePropertyMutator(
                property,
                packageDestination,
                builderClassName
            )
        )
    }

    private fun TypeSpec.Builder.generateBuildFunction(
        builder: FunSpec.Builder,
        constructorParams: MutableList<String>,
        packageDestination: String,
        classBeingBuilt: String
    ) {
        addFunction(
            functionCodeGenerator.generateBuildFunction(
                builder,
                constructorParams,
                packageDestination,
                classBeingBuilt
            )
        )
    }

    private fun updateCosntructorParams(property: KSPropertyDeclaration, constructorParams: MutableList<String>) {
        if (property.isNullable) {
            constructorParams.add(property.simpleName.asString())
        } else {
            constructorParams.add("${property.simpleName.asString()}·?:·throw·IllegalStateException(\"${property.simpleName.asString()}·must·not·be·null!\")")
        }
    }

    private fun packageDestination(classDeclaration: KSClassDeclaration) = "${classDeclaration.packageName.asString()}.autobuilder"
}