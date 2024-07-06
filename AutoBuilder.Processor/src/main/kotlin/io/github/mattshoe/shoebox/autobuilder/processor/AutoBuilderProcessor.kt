package io.github.mattshoe.shoebox.autobuilder.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.*
import io.github.mattshoe.shoebox.autobuilder.annotations.AutoBuilder
import io.github.mattshoe.shoebox.autobuilder.io.github.mattshoe.shoebox.autobuilder.processor.property.ImportStatement
import io.github.mattshoe.shoebox.autobuilder.io.github.mattshoe.shoebox.autobuilder.processor.property.PropertyReader

class AutoBuilderProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val propertyReader: PropertyReader
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(AutoBuilder::class.qualifiedName!!)
        symbols.filterIsInstance<KSClassDeclaration>().forEach { classDeclaration ->
            generateBuilderClass(classDeclaration, resolver)
        }
        return emptyList()
    }

    private fun generateBuilderClass(classDeclaration: KSClassDeclaration, resolver: Resolver) {
        val packageName = packageDestination(classDeclaration)
        val className = classDeclaration.simpleName.asString()
        val builderClassName = "${className}Builder"
        val imports = mutableSetOf<ImportStatement>()

        val builderClass = TypeSpec.classBuilder(builderClassName)
            .addModifiers(KModifier.PUBLIC)
            .apply {
                val buildFunction = FunSpec
                    .builder("build")
                    .returns(ClassName(packageName, className))
                val paramList = mutableListOf<String>()

                classDeclaration.getAllProperties().forEach { property ->
                    val propertyDefinition = propertyReader.getDefaultValue(property, resolver)
                    imports.addAll(propertyDefinition.imports)
                    addProperty(
                        PropertySpec.builder(property.simpleName.asString(), property.type.toTypeName().copy(nullable = property.isNullable))
                            .mutable(true)
                            .initializer(
                                propertyDefinition.value
                            )
                            .addModifiers(KModifier.PRIVATE)
                            .build()
                    )
                    addFunction(
                        FunSpec.builder(property.simpleName.asString())
                            .addParameter(property.simpleName.asString(), property.type.toTypeName().copy(nullable = property.isNullable))
                            .returns(ClassName(packageName, builderClassName))
                            .addStatement("this.${property.simpleName.asString()}·=·${property.simpleName.asString()}")
                            .addStatement("return·this")
                            .build()
                    )

                    if (property.isNullable) {
                        paramList.add(property.simpleName.asString())
                    } else {
                        paramList.add("${property.simpleName.asString()}·?:·throw·IllegalStateException(\"${property.simpleName.asString()}·must·not·be·null!\")")
                    }
                }

                addFunction(
                    buildFunction
                        .addStatement(
                            "return %T(\n    ${paramList.joinToString(",\n    ")}\n)",
                            ClassName(packageName, className)
                        )
                        .build()
                )
            }
            .build()

        val fileSpec = FileSpec.builder(packageName, builderClassName).apply {
            indent("    ")
            addType(builderClass)
            addImport(packageName = classDeclaration.packageName.asString(), className)
            imports.forEach {
                addImport(
                    packageName = it.packageName,
                    it.className
                )
            }
        }.build()

        val file = codeGenerator.createNewFile(
            Dependencies(false, classDeclaration.containingFile!!),
            packageName,
            builderClassName
        )

        file.bufferedWriter().use {
            fileSpec.writeTo(it)
        }
    }

    private fun packageDestination(classDeclaration: KSClassDeclaration) = "${classDeclaration.packageName.asString()}.autobuilder"


}