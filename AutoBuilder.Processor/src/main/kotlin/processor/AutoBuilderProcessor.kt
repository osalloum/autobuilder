package io.github.mattshoe.shoebox.autobuilder.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.*
import io.github.mattshoe.shoebox.autobuilder.annotations.AutoBuilder

class AutoBuilderProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(AutoBuilder::class.qualifiedName!!)
        symbols.filterIsInstance<KSClassDeclaration>().forEach { classDeclaration ->
            generateBuilderClass(classDeclaration, resolver)
        }
        return emptyList()
    }

    private fun generateBuilderClass(classDeclaration: KSClassDeclaration, resolver: Resolver) {
        val packageName = "${classDeclaration.packageName.asString()}.autobuilder"
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
                    val defaultValue = getDefaultValue(property, resolver)
                    addProperty(
                        PropertySpec.builder(property.simpleName.asString(), property.type.toTypeName().copy(nullable = property.isNullable()))
                            .mutable(true)
                            .initializer(defaultValue)
                            .addModifiers(KModifier.PRIVATE)
                            .build()
                    )
                    addFunction(
                        FunSpec.builder(property.simpleName.asString())
                            .addParameter(property.simpleName.asString(), property.type.toTypeName().copy(nullable = property.isNullable()))
                            .returns(ClassName(packageName, builderClassName))
                            .addStatement("this.${property.simpleName.asString()}·=·${property.simpleName.asString()}")
                            .addStatement("return·this")
                            .build()
                    )

                    if (property.isNullable()) {
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

    private fun getDefaultValue(property: KSPropertyDeclaration, resolver: Resolver): String {
        property.annotations.forEach { annotation ->
            when (annotation.annotationType.resolve().declaration.qualifiedName?.asString()) {
                qualifiedAnnotation("DefaultInt") -> return default(annotation, property, resolver.builtIns.intType)
                qualifiedAnnotation("DefaultLong") -> return default(annotation, property, resolver.builtIns.longType) { it + "L" }
                qualifiedAnnotation("DefaultFloat") -> return default(annotation, property, resolver.builtIns.floatType) { it + "f" }
                qualifiedAnnotation("DefaultDouble") -> return default(annotation, property, resolver.builtIns.doubleType)
                qualifiedAnnotation("DefaultBoolean") -> return default(annotation, property, resolver.builtIns.booleanType)
                qualifiedAnnotation("DefaultChar") -> return default(annotation, property, resolver.builtIns.charType) { "'$it'" }
                qualifiedAnnotation("DefaultByte") -> return default(annotation, property, resolver.builtIns.byteType)
                qualifiedAnnotation("DefaultShort") -> return default(annotation, property, resolver.builtIns.shortType)
                qualifiedAnnotation("DefaultString") -> return default(annotation, property, resolver.builtIns.stringType) { "\"$it\""}
            }
        }
        return if (property.isNullable()) {
            "null"
        } else when (property.type.resolve()) {
            resolver.builtIns.intType ->  "0"
            resolver.builtIns.longType ->  "0L"
            resolver.builtIns.floatType ->  "0f"
            resolver.builtIns.doubleType ->  "0.0"
            resolver.builtIns.booleanType -> "false"
            resolver.builtIns.charType -> "'0'"
            resolver.builtIns.byteType -> "0"
            resolver.builtIns.shortType -> "0"
            resolver.builtIns.stringType -> "\"\""
            resolver.builtIns.unitType -> "Unit"
            else -> "null"
        }
    }

    private fun KSPropertyDeclaration.isNullable(): Boolean {
        return type.resolve().isMarkedNullable
    }

    private fun default(
        annotation: KSAnnotation,
        property: KSPropertyDeclaration,
        type: KSType,
        transform: (String) -> String = { it }
    ): String {
        if (property.type.resolve() != type)
            throw IllegalStateException("Type mismatch -- Cannot annotate ${property.type.resolve()} with ${annotation.shortName.asString()}!")

        return transform(annotation.arguments.first().value.toString())
    }

    private fun qualifiedAnnotation(name: String): String = "io.github.mattshoe.shoebox.autobuilder.annotations.$name"
}