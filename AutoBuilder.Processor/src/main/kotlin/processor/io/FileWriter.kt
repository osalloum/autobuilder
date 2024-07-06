package io.github.mattshoe.shoebox.autobuilder.processor.io

import com.google.devtools.ksp.symbol.KSClassDeclaration
import io.github.mattshoe.shoebox.autobuilder.processor.model.GeneratedBuilderFile

interface FileWriter {
    fun newFile(classDeclaration: KSClassDeclaration, generatedBuilder: GeneratedBuilderFile)
}

