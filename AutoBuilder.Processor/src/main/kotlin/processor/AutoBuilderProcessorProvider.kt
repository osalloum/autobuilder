package io.github.mattshoe.shoebox.autobuilder.processor

import com.google.devtools.ksp.processing.*
import io.github.mattshoe.shoebox.autobuilder.processor.defaults.DefaultProviderImpl
import io.github.mattshoe.shoebox.autobuilder.processor.generator.BuilderCodeGenerator
import io.github.mattshoe.shoebox.autobuilder.processor.generator.PropertyGenerator

class AutoBuilderProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return AutoBuilderProcessor(
            environment.codeGenerator,
            environment.logger,
            BuilderCodeGenerator(
                DefaultProviderImpl(environment.logger),
                environment.logger,
                PropertyGenerator()
            )
        )
    }
}