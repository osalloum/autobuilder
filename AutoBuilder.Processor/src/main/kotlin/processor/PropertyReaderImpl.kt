package io.github.mattshoe.shoebox.autobuilder.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import io.github.mattshoe.shoebox.autobuilder.annotations.*

class PropertyReaderImpl(
    private val logger: KSPLogger
): PropertyReader {

    private val defaultAnnotations = mapOf<String, (KSAnnotation, KSPropertyDeclaration, Resolver) -> String>(
        DefaultInt::class.qualifiedName!! to ::readIntDefault,
        DefaultLong::class.qualifiedName!! to ::readLongDefault,
        DefaultFloat::class.qualifiedName!! to ::readFloatDefault,
        DefaultDouble::class.qualifiedName!! to ::readDoubleDefault,
        DefaultBoolean::class.qualifiedName!! to ::readBooleanDefault,
        DefaultChar::class.qualifiedName!! to ::readCharDefault,
        DefaultByte::class.qualifiedName!! to ::readByteDefault,
        DefaultShort::class.qualifiedName!! to ::readShortDefault,
        DefaultString::class.qualifiedName!! to ::readStringDefault
    )

    override fun getDefaultValue(property: KSPropertyDeclaration, resolver: Resolver): String {
        return getDefaultedAnnotationAssignment(property, resolver)
            ?: getDefaultPropertyAssignment(property, resolver)
    }

    private fun KSPropertyDeclaration.isNullable(): Boolean {
        return type.resolve().isMarkedNullable
    }

    private fun getDefaultedAnnotationAssignment(property: KSPropertyDeclaration, resolver: Resolver): String? {
        return property.annotations.firstOrNull {
            it.qualifiedName in defaultAnnotations.keys
        }?.let {
            defaultAnnotations[it.qualifiedName]?.invoke(it, property, resolver)
        }
    }

    private fun getDefaultPropertyAssignment(property: KSPropertyDeclaration, resolver: Resolver): String {
        return if (property.isNullable()) {
            "null"
        } else when (property.type.resolve()) {
            resolver.builtIns.intType ->  "0"
            resolver.builtIns.longType ->  "0L"
            resolver.builtIns.floatType ->  "0f"
            resolver.builtIns.doubleType ->  "0.0"
            resolver.builtIns.booleanType -> "false"
            resolver.builtIns.charType -> "'0'"
            resolver.builtIns.byteType -> "0"
            resolver.builtIns.shortType -> "0"
            resolver.builtIns.stringType -> "\"\""
            resolver.builtIns.unitType -> "Unit"
            else -> "null"
        }
    }

    private fun readAnnotatedDefault(
        annotation: KSAnnotation,
        property: KSPropertyDeclaration,
        type: KSType,
        transform: (String) -> String = { it }
    ): String {
        if (property.type.resolve() != type)
            logger.error("Type mismatch -- Cannot annotate ${property.type.resolve()} with ${annotation.shortName.asString()}!", annotation)

        return transform(annotation.arguments.first().value.toString())
    }

    private fun readIntDefault(annotation: KSAnnotation, property: KSPropertyDeclaration, resolver: Resolver): String {
        return readAnnotatedDefault(annotation, property, resolver.builtIns.intType)
    }

    private fun readLongDefault(annotation: KSAnnotation, property: KSPropertyDeclaration, resolver: Resolver): String {
        return readAnnotatedDefault(annotation, property, resolver.builtIns.longType) { it + "L"}
    }

    private fun readFloatDefault(annotation: KSAnnotation, property: KSPropertyDeclaration, resolver: Resolver): String {
        return readAnnotatedDefault(annotation, property, resolver.builtIns.floatType) { it + "f"}
    }

    private fun readDoubleDefault(annotation: KSAnnotation, property: KSPropertyDeclaration, resolver: Resolver): String {
        return readAnnotatedDefault(annotation, property, resolver.builtIns.doubleType)
    }

    private fun readBooleanDefault(annotation: KSAnnotation, property: KSPropertyDeclaration, resolver: Resolver): String {
        return readAnnotatedDefault(annotation, property, resolver.builtIns.booleanType)
    }

    private fun readCharDefault(annotation: KSAnnotation, property: KSPropertyDeclaration, resolver: Resolver): String {
        return readAnnotatedDefault(annotation, property, resolver.builtIns.charType) { "'$it'"}
    }

    private fun readByteDefault(annotation: KSAnnotation, property: KSPropertyDeclaration, resolver: Resolver): String {
        return readAnnotatedDefault(annotation, property, resolver.builtIns.byteType)
    }

    private fun readShortDefault(annotation: KSAnnotation, property: KSPropertyDeclaration, resolver: Resolver): String {
        return readAnnotatedDefault(annotation, property, resolver.builtIns.shortType)
    }

    private fun readStringDefault(annotation: KSAnnotation, property: KSPropertyDeclaration, resolver: Resolver): String {
        return readAnnotatedDefault(annotation, property, resolver.builtIns.stringType) { "\"$it\""}
    }
}