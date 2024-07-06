package io.github.mattshoe.shoebox.autobuilder.processor.io

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import io.github.mattshoe.shoebox.autobuilder.processor.model.GeneratedBuilderFile

class FileWriter(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) {
    fun newFile(classDeclaration: KSClassDeclaration, generatedBuilder: GeneratedBuilderFile) {
        try {
            val file = codeGenerator.createNewFile(
                Dependencies(false, classDeclaration.containingFile!!),
                generatedBuilder.packageDestination,
                generatedBuilder.builderClassName
            )

            file.bufferedWriter().use {
                generatedBuilder.fileSpec.writeTo(it)
            }
        } catch (e: Throwable) {
            logger.error("AutoBuilder could not write new file:  ${generatedBuilder.packageDestination}.${generatedBuilder.className}", classDeclaration)
        }
    }
}