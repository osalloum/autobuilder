package io.github.mattshoe.shoebox.autobuilder.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import io.github.mattshoe.shoebox.autobuilder.annotations.AutoBuilder
import io.github.mattshoe.shoebox.autobuilder.processor.generator.BuilderClassCodeGenerator
import io.github.mattshoe.shoebox.autobuilder.processor.io.FileWriter

class AutoBuilderProcessor(
    private val builderClassCodeGenerator: BuilderClassCodeGenerator,
    private val fileWriter: FileWriter
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        processAllAutoBuilderSymbols(resolver)

        return emptyList() // We don't need to postpone any processing, we have no dependencies.
    }

    private fun processAllAutoBuilderSymbols(resolver: Resolver) {
        resolver
            .getSymbolsWithAnnotation(AutoBuilder::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .forEach { classDeclaration ->
                generateBuilderClass(classDeclaration, resolver)
            }
    }

    private fun generateBuilderClass(classDeclaration: KSClassDeclaration, resolver: Resolver) {
        fileWriter.newFile(
            classDeclaration,
            builderClassCodeGenerator.generateBuilderCodeFor(classDeclaration, resolver)
        )
    }

}

