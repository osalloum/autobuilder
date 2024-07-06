package io.github.mattshoe.shoebox.autobuilder.processor

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSPropertyDeclaration

interface PropertyReader {
    fun getDefaultValue(property: KSPropertyDeclaration, resolver: Resolver): String
}

