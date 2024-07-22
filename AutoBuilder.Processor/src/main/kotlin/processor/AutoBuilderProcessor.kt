package io.github.mattshoe.shoebox.autobuilder.processor

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSNode
import io.github.mattshoe.shoebox.autobuilder.annotations.AutoBuilder
import io.github.mattshoe.shoebox.autobuilder.processor.defaults.DefaultProviderImpl
import io.github.mattshoe.shoebox.autobuilder.processor.generator.function.FunctionCodeGeneratorImpl
import io.github.mattshoe.shoebox.autobuilder.processor.generator.property.PropertyCodeGeneratorImpl
import io.github.mattshoe.shoebox.stratify.StratifySymbolProcessor
import io.github.mattshoe.shoebox.stratify.strategy.AnnotationStrategy
import io.github.mattshoe.shoebox.stratify.strategy.Strategy

class AutoBuilderProcessor(
    environment: SymbolProcessorEnvironment
): StratifySymbolProcessor(
    environment
) {

    override suspend fun buildStrategies(resolver: Resolver): List<Strategy<KSNode, out KSNode>>  = listOf(
        AnnotationStrategy(
            AutoBuilder::class,
            AutoBuilderClassProcessor(
                resolver,
                PropertyCodeGeneratorImpl(),
                FunctionCodeGeneratorImpl(),
                DefaultProviderImpl(environment.logger),
                environment.logger
            )
        )
    )
}

