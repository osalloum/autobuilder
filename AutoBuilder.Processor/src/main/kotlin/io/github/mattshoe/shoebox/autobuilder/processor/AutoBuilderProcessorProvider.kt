package io.github.mattshoe.shoebox.autobuilder.processor

import com.google.devtools.ksp.processing.*
import io.github.mattshoe.shoebox.autobuilder.io.github.mattshoe.shoebox.autobuilder.processor.property.PropertyReaderImpl

class AutoBuilderProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return AutoBuilderProcessor(
            environment.codeGenerator,
            environment.logger,
            PropertyReaderImpl(environment.logger)
        )
    }
}