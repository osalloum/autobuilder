package io.github.mattshoe.shoebox.autobuilder.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import io.github.mattshoe.shoebox.autobuilder.annotations.AutoBuilder
import io.github.mattshoe.shoebox.autobuilder.processor.generator.BuilderCodeGenerator

class AutoBuilderProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val builderCodeGenerator: BuilderCodeGenerator
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(AutoBuilder::class.qualifiedName!!)
        symbols.filterIsInstance<KSClassDeclaration>().forEach { classDeclaration ->
            generateBuilderClass(classDeclaration, resolver)
        }
        return emptyList()
    }

    private fun generateBuilderClass(classDeclaration: KSClassDeclaration, resolver: Resolver) {
        val generatedBuilder = builderCodeGenerator.generateBuilderCodeFor(classDeclaration, resolver)

        val file = codeGenerator.createNewFile(
            Dependencies(false, classDeclaration.containingFile!!),
            generatedBuilder.packageDestination,
            generatedBuilder.builderClassName
        )

        file.bufferedWriter().use {
            generatedBuilder.fileSpec.writeTo(it)
        }
    }
    
}