package io.github.mattshoe.shoebox.autobuilder.processor

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSPropertyDeclaration

val KSPropertyDeclaration.isNullable: Boolean
    get() = type.resolve().isMarkedNullable

val KSAnnotation.qualifiedName: String
    get() = annotationType.resolve().declaration.qualifiedName?.asString() ?: "UNKNOWN"