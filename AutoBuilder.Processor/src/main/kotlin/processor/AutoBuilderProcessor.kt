package io.github.mattshoe.shoebox.autobuilder.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import io.github.mattshoe.shoebox.autobuilder.annotations.AutoBuilder
import io.github.mattshoe.shoebox.autobuilder.processor.generator.BuilderCodeGenerator
import io.github.mattshoe.shoebox.autobuilder.processor.io.FileWriter

class AutoBuilderProcessor(
    private val codeGenerator: CodeGenerator,
    private val builderCodeGenerator: BuilderCodeGenerator,
    private val fileWriter: FileWriter
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(AutoBuilder::class.qualifiedName!!)
        symbols.filterIsInstance<KSClassDeclaration>().forEach { classDeclaration ->
            generateBuilderClass(classDeclaration, resolver)
        }
        return emptyList()
    }

    private fun generateBuilderClass(classDeclaration: KSClassDeclaration, resolver: Resolver) {
        fileWriter.newFile(
            classDeclaration,
            builderCodeGenerator.generateBuilderCodeFor(classDeclaration, resolver)
        )
    }

}

