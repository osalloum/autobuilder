package io.github.mattshoe.shoebox.autobuilder.processor

import com.google.devtools.ksp.processing.*
import io.github.mattshoe.shoebox.autobuilder.processor.defaults.DefaultProviderImpl
import io.github.mattshoe.shoebox.autobuilder.processor.generator.BuilderClassCodeGenerator
import io.github.mattshoe.shoebox.autobuilder.processor.generator.FunctionCodeGenerator
import io.github.mattshoe.shoebox.autobuilder.processor.generator.PropertyCodeGenerator
import io.github.mattshoe.shoebox.autobuilder.processor.io.FileWriter

class AutoBuilderProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return AutoBuilderProcessor(
            BuilderClassCodeGenerator(
                DefaultProviderImpl(environment.logger),
                PropertyCodeGenerator(),
                FunctionCodeGenerator()
            ),
            FileWriter(
                environment.codeGenerator,
                environment.logger
            )
        )
    }
}