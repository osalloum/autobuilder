package io.github.mattshoe.shoebox.autobuilder.processor

import com.google.devtools.ksp.processing.*
import io.github.mattshoe.shoebox.autobuilder.processor.defaults.DefaultProviderImpl
import io.github.mattshoe.shoebox.autobuilder.processor.generator.builderclass.BuilderClassCodeGenerator
import io.github.mattshoe.shoebox.autobuilder.processor.generator.builderclass.BuilderClassCodeGeneratorImpl
import io.github.mattshoe.shoebox.autobuilder.processor.generator.function.FunctionCodeGenerator
import io.github.mattshoe.shoebox.autobuilder.processor.generator.function.FunctionCodeGeneratorImpl
import io.github.mattshoe.shoebox.autobuilder.processor.generator.property.PropertyCodeGenerator
import io.github.mattshoe.shoebox.autobuilder.processor.generator.property.PropertyCodeGeneratorImpl
import io.github.mattshoe.shoebox.autobuilder.processor.io.FileWriter
import io.github.mattshoe.shoebox.autobuilder.processor.io.FileWriterImpl

class AutoBuilderProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return AutoBuilderProcessor(
            BuilderClassCodeGeneratorImpl(
                DefaultProviderImpl(environment.logger),
                PropertyCodeGeneratorImpl(),
                FunctionCodeGeneratorImpl()
            ),
            FileWriterImpl(
                environment.codeGenerator,
                environment.logger
            )
        )
    }
}