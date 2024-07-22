package io.github.mattshoe.shoebox.autobuilder.processor

import com.google.devtools.ksp.processing.SymbolProcessorProvider
import io.github.mattshoe.shoebox.stratify.stratifyProvider

class AutoBuilderProcessorProvider : SymbolProcessorProvider by stratifyProvider<AutoBuilderProcessor>()