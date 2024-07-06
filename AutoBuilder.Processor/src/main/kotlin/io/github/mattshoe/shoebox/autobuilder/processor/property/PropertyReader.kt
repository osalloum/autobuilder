package io.github.mattshoe.shoebox.autobuilder.io.github.mattshoe.shoebox.autobuilder.processor.property

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSPropertyDeclaration

interface PropertyReader {
    fun getDefaultValue(property: KSPropertyDeclaration, resolver: Resolver): PropertyDefinition
}

