package io.github.mattshoe.shoebox.autobuilder.processor

import com.google.devtools.ksp.processing.*
import io.github.mattshoe.shoebox.autobuilder.processor.defaults.DefaultProviderImpl
import io.github.mattshoe.shoebox.autobuilder.processor.generator.BuilderCodeGenerator
import io.github.mattshoe.shoebox.autobuilder.processor.generator.PropertyGenerator
import io.github.mattshoe.shoebox.autobuilder.processor.io.FileWriter

class AutoBuilderProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return AutoBuilderProcessor(
            environment.codeGenerator,
            BuilderCodeGenerator(
                DefaultProviderImpl(environment.logger),
                environment.logger,
                PropertyGenerator()
            ),
            FileWriter(
                environment.codeGenerator,
                environment.logger
            )
        )
    }
}