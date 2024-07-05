package io.github.mattshoe.shoebox.autobuilder.processor

import com.google.devtools.ksp.processing.*

class AutoBuilderProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return AutoBuilderProcessor(environment.codeGenerator, environment.logger)
    }
}