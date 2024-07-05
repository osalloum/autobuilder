package io.github.mattshoe.shoebox.autobuilder.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.*
import io.github.mattshoe.shoebox.autobuilder.annotations.AutoBuilder

class AutoBuilderProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val propertyReader: PropertyReader
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(AutoBuilder::class.qualifiedName!!)
        symbols.filterIsInstance<KSClassDeclaration>().forEach { classDeclaration ->
            generateBuilderClass(classDeclaration, resolver)
            generateExtensionFunction(classDeclaration)
        }
        return emptyList()
    }

    private fun generateBuilderClass(classDeclaration: KSClassDeclaration, resolver: Resolver) {
        val packageName = packageDestination(classDeclaration)
        val className = classDeclaration.simpleName.asString()
        val builderClassName = "${className}Builder"

        val builderClass = TypeSpec.classBuilder(builderClassName)
            .addModifiers(KModifier.PUBLIC)
            .apply {
                val buildFunction = FunSpec
                    .builder("build")
                    .returns(ClassName(packageName, className))
                val paramList = mutableListOf<String>()

                classDeclaration.getAllProperties().forEach { property ->
                    addProperty(
                        PropertySpec.builder(property.simpleName.asString(), property.type.toTypeName().copy(nullable = property.isNullable))
                            .mutable(true)
                            .initializer(
                                propertyReader.getDefaultValue(property, resolver)
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
                            "return %T(\n${paramList.joinToString(",\n")}\n)",
                            ClassName(packageName, className)
                        )
                        .build()
                )
            }
            .build()

        val fileSpec = FileSpec.builder(packageName, builderClassName)
            .addType(builderClass)
            .addImport(packageName = classDeclaration.packageName.asString(), className)
            .build()

        val file = codeGenerator.createNewFile(
            Dependencies(false, classDeclaration.containingFile!!),
            packageName,
            builderClassName
        )

        file.bufferedWriter().use {
            fileSpec.writeTo(it)
        }
    }

    private fun generateExtensionFunction(classDeclaration: KSClassDeclaration) {
        val packageName = packageDestination(classDeclaration)
        val className = classDeclaration.simpleName.asString()
        val builderClassName = "${className}Builder"
        val classType = ClassName(packageName, className)
        val builderType = ClassName(packageName, builderClassName)

        val functionSpec = FunSpec.builder("builder")
            .receiver(classType)
            .returns(builderType)
            .addStatement("return %T()", builderType)
            .build()

        val fileSpec = FileSpec.builder(packageName, "BuilderExtensions")
            .addFunction(functionSpec)
            .addImport(packageName = classDeclaration.packageName.asString(), className)
            .build()

        val file = codeGenerator.createNewFile(
            Dependencies(false, classDeclaration.containingFile!!),
            packageName,
            "BuilderExtensions"
        )

        file.bufferedWriter().use {
            fileSpec.writeTo(it)
        }
    }

    private fun packageDestination(classDeclaration: KSClassDeclaration) = "${classDeclaration.packageName.asString()}.autobuilder"


}