package io.github.mattshoe.shoebox.autobuilder.processor.defaults

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import io.github.mattshoe.shoebox.autobuilder.processor.model.PropertyData

interface DefaultProvider {
    fun defaultPropertyValue(property: KSPropertyDeclaration, resolver: Resolver): PropertyData
}

