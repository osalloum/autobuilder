package io.github.mattshoe.shoebox.autobuilder.processor.generator.builderclass

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import io.github.mattshoe.shoebox.autobuilder.processor.model.GeneratedBuilderFile

interface BuilderClassCodeGenerator {
    fun generateBuilderCodeFor(classDeclaration: KSClassDeclaration, resolver: Resolver): GeneratedBuilderFile
}

