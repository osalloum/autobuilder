package io.github.mattshoe.shoebox.autobuilder.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.visitor.KSTopDownVisitor
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import io.github.mattshoe.shoebox.autobuilder.processor.defaults.DefaultProvider
import io.github.mattshoe.shoebox.autobuilder.processor.generator.function.FunctionCodeGenerator
import io.github.mattshoe.shoebox.autobuilder.processor.generator.property.PropertyCodeGenerator
import io.github.mattshoe.shoebox.autobuilder.processor.model.ImportStatement
import io.github.mattshoe.shoebox.stratify.model.GeneratedFile
import io.github.mattshoe.shoebox.stratify.processor.Processor

class AutoBuilderClassProcessor(
    private val resolver: Resolver,
    private val propertyCodeGenerator: PropertyCodeGenerator,
    private val functionCodeGenerator: FunctionCodeGenerator,
    private val defaultProvider: DefaultProvider,
    private val logger: KSPLogger
): Processor<KSClassDeclaration> {
    override val targetClass = KSClassDeclaration::class

    inner class Visitor: KSVisitorVoid() {
        override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
            super.visitFunctionDeclaration(function, data)
        }
    }

    override suspend fun process(node: KSClassDeclaration): Set<GeneratedFile> {
        val packageDestination = packageDestination(node)
        val classBeingBuilt = node.simpleName.asString()
        val builderClassName = "${classBeingBuilt}Builder"
        val builderImports = mutableSetOf<ImportStatement>()

        logger.warn("Processing $classBeingBuilt")

        val builderClass = generateBuilderClass(
            builderClassName,
            packageDestination,
            classBeingBuilt,
            node,
            resolver,
            builderImports
        )

        return setOf(
            GeneratedFile(
                fileName = builderClassName,
                packageName = packageDestination,
                output = generateFileSpec(
                    node,
                    packageDestination,
                    classBeingBuilt,
                    builderClassName,
                    builderClass,
                    builderImports
                ).toString()
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
            functionCodeGenerator.generatePropertyMutatorFunction(
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