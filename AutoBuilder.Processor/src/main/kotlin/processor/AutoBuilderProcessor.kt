package io.github.mattshoe.shoebox.autobuilder.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.*
import io.github.mattshoe.shoebox.autobuilder.annotations.AutoBuilder

class AutoBuilderProcessor(private val codeGenerator: CodeGenerator, private val logger: KSPLogger) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(AutoBuilder::class.qualifiedName!!)
        symbols.filterIsInstance<KSClassDeclaration>().forEach { classDeclaration ->
            generateBuilderClass(classDeclaration)
        }
        return emptyList()
    }

    private fun generateBuilderClass(classDeclaration: KSClassDeclaration) {
        val packageName = classDeclaration.packageName.asString()
        val className = classDeclaration.simpleName.asString()
        val builderClassName = "${className}Builder"

        val builderClass = TypeSpec.classBuilder(builderClassName)
            .addModifiers(KModifier.PUBLIC)
            .apply {
                classDeclaration.getAllProperties().forEach { property ->
                    val defaultValue = getDefaultValue(property)
                    addProperty(
                        PropertySpec.builder(property.simpleName.asString(), property.type.toTypeName().copy(nullable = true))
                            .mutable(true)
                            .initializer(defaultValue ?: "null")
                            .build()
                    )
                    addFunction(
                        FunSpec.builder(property.simpleName.asString())
                            .addParameter(property.simpleName.asString(), property.type.toTypeName().copy(nullable = true))
                            .returns(ClassName(packageName, builderClassName))
                            .addStatement("this.${property.simpleName} = ${property.simpleName}")
                            .addStatement("return this")
                            .build()
                    )
                }
                addFunction(
                    FunSpec.builder("build")
                        .returns(ClassName(packageName, className))
                        .addStatement(
                            "return %T(${classDeclaration.getAllProperties().joinToString(", ") { property ->
                                "${property.simpleName} ?: throw IllegalArgumentException(\"${property.simpleName} must not be null\")"
                            }})",
                            ClassName(packageName, className)
                        )
                        .build()
                )
            }
            .build()

        val fileSpec = FileSpec.builder(packageName, builderClassName)
            .addType(builderClass)
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

    private fun getDefaultValue(property: KSPropertyDeclaration): String? {
        property.annotations.forEach { annotation ->
            when (annotation.shortName.asString()) {
                "DefaultInt" -> return annotation.arguments.first().value.toString()
                "DefaultLong" -> return annotation.arguments.first().value.toString() + "L"
                "DefaultFloat" -> return annotation.arguments.first().value.toString() + "f"
                "DefaultDouble" -> return annotation.arguments.first().value.toString()
                "DefaultBoolean" -> return annotation.arguments.first().value.toString()
                "DefaultChar" -> return "'${annotation.arguments.first().value}'"
                "DefaultByte" -> return annotation.arguments.first().value.toString()
                "DefaultShort" -> return annotation.arguments.first().value.toString()
                "DefaultString" -> return "\"${annotation.arguments.first().value}\""
            }
        }
        return null
    }
}