package io.github.mattshoe.shoebox.autobuilder.processor.generator

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toTypeName
import io.github.mattshoe.shoebox.autobuilder.processor.defaults.DefaultProvider
import io.github.mattshoe.shoebox.autobuilder.processor.model.ImportStatement
import io.github.mattshoe.shoebox.autobuilder.processor.isNullable

data class GeneratedBuilderFile(
    val packageDestination: String,
    val className: String,
    val builderClassName: String,
    val fileSpec: FileSpec
)

class BuilderCodeGenerator(
    private val defaultProvider: DefaultProvider,
    private val logger: KSPLogger,
    private val propertyGenerator: PropertyGenerator
) {
    fun generateBuilderCodeFor(classDeclaration: KSClassDeclaration, resolver: Resolver): GeneratedBuilderFile {
        val packageDestination = packageDestination(classDeclaration)
        val className = classDeclaration.simpleName.asString()
        val builderClassName = "${className}Builder"
        val builderImports = mutableSetOf<ImportStatement>()

        val builderClass = TypeSpec.classBuilder(builderClassName)
            .addModifiers(KModifier.PUBLIC)
            .apply {
                val buildFunction = FunSpec
                    .builder("build")
                    .returns(ClassName(packageDestination, className))
                val paramList = mutableListOf<String>()

                classDeclaration.getAllProperties().forEach { property ->
                    
                    with (defaultProvider.defaultPropertyValue(property, resolver)) {
                        builderImports.addAll(this.imports)
                        addProperty(
                            propertyGenerator.generatePropertyCodeFor(property, this)
                        )
                    }
                    
                    addFunction(
                        FunSpec.builder(property.simpleName.asString())
                            .addParameter(property.simpleName.asString(), property.type.toTypeName().copy(nullable = property.isNullable))
                            .returns(ClassName(packageDestination, builderClassName))
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
                            ClassName(packageDestination, className)
                        )
                        .build()
                )
            }
            .build()

        return GeneratedBuilderFile(
            packageDestination,
            className,
            builderClassName,
            FileSpec.builder(packageDestination, builderClassName).apply {
                indent("    ")
                addType(builderClass)
                addImport(packageName = classDeclaration.packageName.asString(), className)
                builderImports.forEach {
                    addImport(
                        packageName = it.packageName,
                        it.className
                    )
                }
            }.build()
        )
    }

    private fun packageDestination(classDeclaration: KSClassDeclaration) = "${classDeclaration.packageName.asString()}.autobuilder"
}