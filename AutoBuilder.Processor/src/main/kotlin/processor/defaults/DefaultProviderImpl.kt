package io.github.mattshoe.shoebox.autobuilder.processor.defaults

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import io.github.mattshoe.shoebox.autobuilder.annotations.*
import io.github.mattshoe.shoebox.autobuilder.processor.model.ImportStatement
import io.github.mattshoe.shoebox.autobuilder.processor.model.PropertyData
import io.github.mattshoe.shoebox.autobuilder.processor.qualifiedName

class DefaultProviderImpl(
    private val logger: KSPLogger
): DefaultProvider {

    private val defaultAnnotations = mapOf<String, (KSAnnotation, KSPropertyDeclaration, Resolver) -> PropertyData>(
        DefaultInt::class.qualifiedName!! to ::readIntDefault,
        DefaultLong::class.qualifiedName!! to ::readLongDefault,
        DefaultFloat::class.qualifiedName!! to ::readFloatDefault,
        DefaultDouble::class.qualifiedName!! to ::readDoubleDefault,
        DefaultBoolean::class.qualifiedName!! to ::readBooleanDefault,
        DefaultChar::class.qualifiedName!! to ::readCharDefault,
        DefaultByte::class.qualifiedName!! to ::readByteDefault,
        DefaultShort::class.qualifiedName!! to ::readShortDefault,
        DefaultString::class.qualifiedName!! to ::readStringDefault,
        Default::class.qualifiedName!! to ::readNonPrimitiveDefaultAnnotation
    )

    override fun defaultPropertyValue(property: KSPropertyDeclaration, resolver: Resolver): PropertyData {

        return getDefaultedAnnotationAssignment(property, resolver)
            ?: getDefaultPropertyAssignment(property, resolver)
    }

    private fun KSPropertyDeclaration.isNullable(): Boolean {
        return type.resolve().isMarkedNullable
    }

    private fun getDefaultedAnnotationAssignment(property: KSPropertyDeclaration, resolver: Resolver): PropertyData? {
        return property.annotations.firstOrNull {
            it.qualifiedName in defaultAnnotations.keys
        }?.let {
            defaultAnnotations[it.qualifiedName]?.invoke(it, property, resolver)
        }
    }

    private fun getDefaultPropertyAssignment(property: KSPropertyDeclaration, resolver: Resolver): PropertyData {
        return PropertyData(
            value = when {
                property.isNullable() -> "null"
                property.isPrimitive(resolver) -> getSystemDefaultPrimitives(property, resolver)
                else -> getEmptyConstructorDefault(property)
            },
            imports = emptyList()
        )
    }

    private fun readPrimitiveDefaultAnnotation(
        annotation: KSAnnotation,
        property: KSPropertyDeclaration,
        type: KSType,
        transform: (String) -> String = { it }
    ): PropertyData {
        if (property.type.resolve().makeNotNullable() != type)
            logger.error("Type mismatch -- Cannot annotate ${property.type.resolve()} with ${annotation.shortName.asString()}!", annotation)

        return PropertyData(
            transform(annotation.arguments.first().value.toString()),
            emptyList()
        )
    }

    private fun readIntDefault(annotation: KSAnnotation, property: KSPropertyDeclaration, resolver: Resolver): PropertyData {
        return readPrimitiveDefaultAnnotation(annotation, property, resolver.builtIns.intType)
    }

    private fun readLongDefault(annotation: KSAnnotation, property: KSPropertyDeclaration, resolver: Resolver): PropertyData {
        return readPrimitiveDefaultAnnotation(annotation, property, resolver.builtIns.longType) { it + "L"}
    }

    private fun readFloatDefault(annotation: KSAnnotation, property: KSPropertyDeclaration, resolver: Resolver): PropertyData {
        return readPrimitiveDefaultAnnotation(annotation, property, resolver.builtIns.floatType) { it + "f"}
    }

    private fun readDoubleDefault(annotation: KSAnnotation, property: KSPropertyDeclaration, resolver: Resolver): PropertyData {
        return readPrimitiveDefaultAnnotation(annotation, property, resolver.builtIns.doubleType)
    }

    private fun readBooleanDefault(annotation: KSAnnotation, property: KSPropertyDeclaration, resolver: Resolver): PropertyData {
        return readPrimitiveDefaultAnnotation(annotation, property, resolver.builtIns.booleanType)
    }

    private fun readCharDefault(annotation: KSAnnotation, property: KSPropertyDeclaration, resolver: Resolver): PropertyData {
        return readPrimitiveDefaultAnnotation(annotation, property, resolver.builtIns.charType) { "'$it'"}
    }

    private fun readByteDefault(annotation: KSAnnotation, property: KSPropertyDeclaration, resolver: Resolver): PropertyData {
        return readPrimitiveDefaultAnnotation(annotation, property, resolver.builtIns.byteType)
    }

    private fun readShortDefault(annotation: KSAnnotation, property: KSPropertyDeclaration, resolver: Resolver): PropertyData {
        return readPrimitiveDefaultAnnotation(annotation, property, resolver.builtIns.shortType)
    }

    private fun readStringDefault(annotation: KSAnnotation, property: KSPropertyDeclaration, resolver: Resolver): PropertyData {
        return readPrimitiveDefaultAnnotation(annotation, property, resolver.builtIns.stringType) { "\"$it\""}
    }

    private fun readNonPrimitiveDefaultAnnotation(annotation: KSAnnotation, property: KSPropertyDeclaration, resolver: Resolver): PropertyData {
        if (property.isPrimitive(resolver))
            logger.error("Cannot use the @Default(..) annotation on a primitive type! Use the corresponding primitive annotation instead.", annotation)

        val args =  annotation.arguments.find { it.name?.asString() == "args" }?.value as ArrayList<String>
        val imports = annotation.arguments.find { it.name?.asString() == "imports" }?.value  as ArrayList<String>

        val propertyType = property.type.resolve().declaration.simpleName.asString()
        val propertyAssignment = "$propertyType(${args.joinToString(",\n    ")})"

        return PropertyData(
            value = propertyAssignment,
            imports = imports.mapNotNull {
                it.toImportStatement()
            }
        )
    }

    private fun KSPropertyDeclaration.isPrimitive(resolver: Resolver): Boolean {
        return type.resolve().makeNotNullable() in listOf(
            resolver.builtIns.booleanType,
            resolver.builtIns.byteType,
            resolver.builtIns.charType,
            resolver.builtIns.doubleType,
            resolver.builtIns.floatType,
            resolver.builtIns.intType,
            resolver.builtIns.longType,
            resolver.builtIns.shortType,
            resolver.builtIns.stringType
        )
    }

    private fun String.toImportStatement(): ImportStatement? {
        val lastDotIndex = lastIndexOf('.')

        return if (lastDotIndex != -1) {
            ImportStatement(
                substring(0, lastDotIndex),
                substring(lastDotIndex + 1)
            )
        } else null
    }

    private fun getSystemDefaultPrimitives(property: KSPropertyDeclaration, resolver: Resolver): String {
        return when (property.type.resolve()) {
            resolver.builtIns.intType ->  "0"
            resolver.builtIns.longType ->  "0L"
            resolver.builtIns.floatType ->  "0f"
            resolver.builtIns.doubleType ->  "0.0"
            resolver.builtIns.booleanType -> "false"
            resolver.builtIns.charType -> "'\\u0000'"
            resolver.builtIns.byteType -> "0"
            resolver.builtIns.shortType -> "0"
            resolver.builtIns.stringType -> "\"\""
            resolver.builtIns.unitType -> "Unit"
            else -> "null"
        }
    }

    private fun getEmptyConstructorDefault(property: KSPropertyDeclaration): String {
        val propertyType = property.type.resolve().declaration.simpleName.asString()
        return "$propertyType()"
    }
}