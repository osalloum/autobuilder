package processor

import com.tschuchort.compiletesting.*
import io.github.mattshoe.shoebox.autobuilder.processor.AutoBuilderProcessorProvider
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCompilerApi::class)
class AutoBuilderProcessorTest {

    @Test
    fun `properties with incorrect default annotation reports error`() {
        val kspCompileResult = compile(
            SourceFile.kotlin(
                "BorkedDefault.kt",
                """
                package io.github.mattshoe.shoebox
    
                import io.github.mattshoe.shoebox.autobuilder.annotations.*
    
                @AutoBuilder
                data class BorkedDefault(
                    @DefaultString("how dare you")
                    val bar: Int,
                )
            """
            )
        )

        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, kspCompileResult.result.exitCode)
        assertTrue {
            kspCompileResult.result.messages.contains(
                "Type mismatch -- Cannot annotate Int with DefaultString!"
            )
        }
    }

    @Test
    fun `mix of defaulted and non-defaulted properties are formatted correctly`() {
        assertBuilderOutput(
            "MultiPropData",

            """
                package io.github.mattshoe.shoebox
    
                import io.github.mattshoe.shoebox.autobuilder.annotations.*
    
                @AutoBuilder
                data class MultiPropData(
                    @DefaultInt(42)
                    val int: Int?,
                    val bool: Boolean,
                    @DefaultString("derp")
                    val string: String?,
                    val char: Char
                )
            """,

            """
                package io.github.mattshoe.shoebox.autobuilder
                
                import io.github.mattshoe.shoebox.MultiPropData
                import kotlin.Boolean
                import kotlin.Char
                import kotlin.Int
                import kotlin.String
                
                public class MultiPropDataBuilder {
                    private var int: Int? = 42
                
                    private var bool: Boolean = false
                
                    private var string: String? = "derp"
                
                    private var char: Char = '0'
                
                    public fun int(int: Int?): MultiPropDataBuilder {
                        this.int = int
                        return this
                    }
                
                    public fun bool(bool: Boolean): MultiPropDataBuilder {
                        this.bool = bool
                        return this
                    }
                
                    public fun string(string: String?): MultiPropDataBuilder {
                        this.string = string
                        return this
                    }
                
                    public fun char(char: Char): MultiPropDataBuilder {
                        this.char = char
                        return this
                    }
                
                    public fun build(): MultiPropData = MultiPropData(
                        int,
                        bool ?: throw IllegalStateException("bool must not be null!"),
                        string,
                        char ?: throw IllegalStateException("char must not be null!")
                    )
                }
        """.trimIndent()
        )
    }

    @Test
    fun `multiple defaulted properties are formatted correctly`() {
        assertBuilderOutput(
            "MultiPropData",

            """
                package io.github.mattshoe.shoebox
    
                import io.github.mattshoe.shoebox.autobuilder.annotations.*
    
                @AutoBuilder
                data class MultiPropData(
                    @DefaultInt(42)
                    val int: Int?,
                    @DefaultBoolean(true)
                    val bool: Boolean,
                    @DefaultString("derp")
                    val string: String?,
                    @DefaultChar("w")
                    val char: Char
                )
            """,

            """
                package io.github.mattshoe.shoebox.autobuilder
                
                import io.github.mattshoe.shoebox.MultiPropData
                import kotlin.Boolean
                import kotlin.Char
                import kotlin.Int
                import kotlin.String
                
                public class MultiPropDataBuilder {
                    private var int: Int? = 42
                
                    private var bool: Boolean = true
                
                    private var string: String? = "derp"
                
                    private var char: Char = 'w'
                
                    public fun int(int: Int?): MultiPropDataBuilder {
                        this.int = int
                        return this
                    }
                
                    public fun bool(bool: Boolean): MultiPropDataBuilder {
                        this.bool = bool
                        return this
                    }
                
                    public fun string(string: String?): MultiPropDataBuilder {
                        this.string = string
                        return this
                    }
                
                    public fun char(char: Char): MultiPropDataBuilder {
                        this.char = char
                        return this
                    }
                
                    public fun build(): MultiPropData = MultiPropData(
                        int,
                        bool ?: throw IllegalStateException("bool must not be null!"),
                        string,
                        char ?: throw IllegalStateException("char must not be null!")
                    )
                }
        """.trimIndent()
        )
    }

    @Test
    fun `multiple non-defaulted properties are formatted correctly`() {
        assertBuilderOutput(
            "MultiPropData",

            """
                package io.github.mattshoe.shoebox
    
                import io.github.mattshoe.shoebox.autobuilder.annotations.*
    
                @AutoBuilder
                data class MultiPropData(
                    val int: Int?,
                    val bool: Boolean,
                    val string: String?,
                    val char: Char
                )
            """,

            """
                package io.github.mattshoe.shoebox.autobuilder
                
                import io.github.mattshoe.shoebox.MultiPropData
                import kotlin.Boolean
                import kotlin.Char
                import kotlin.Int
                import kotlin.String
                
                public class MultiPropDataBuilder {
                    private var int: Int? = null
                
                    private var bool: Boolean = false
                
                    private var string: String? = null
                
                    private var char: Char = '0'
                
                    public fun int(int: Int?): MultiPropDataBuilder {
                        this.int = int
                        return this
                    }
                
                    public fun bool(bool: Boolean): MultiPropDataBuilder {
                        this.bool = bool
                        return this
                    }
                
                    public fun string(string: String?): MultiPropDataBuilder {
                        this.string = string
                        return this
                    }
                
                    public fun char(char: Char): MultiPropDataBuilder {
                        this.char = char
                        return this
                    }
                
                    public fun build(): MultiPropData = MultiPropData(
                        int,
                        bool ?: throw IllegalStateException("bool must not be null!"),
                        string,
                        char ?: throw IllegalStateException("char must not be null!")
                    )
                }
        """.trimIndent()
        )
    }

// region Integer tests

    @Test
    fun `WHEN non-null Int property with no annotated default THEN correct primitive system default is given`() {
        nonNullPrimitiveTest("Int", "0")
    }

    @Test
    fun `WHEN non-null Int property with annotated default THEN specified default is used`() {
        nonNullPrimitiveWithDefaultTest("Int", "42")
    }

    @Test
    fun `WHEN nullable Int property with no annotated default THEN builder property defaults to null`() {
        nullablePrimitiveTest("Int")
    }

    @Test
    fun `WHEN nullable Int property with annotated default THEN builder property uses specified default`() {
        nullablePrimitiveWithDefaultTest("Int", "42")
    }

// endregion

// region Long tests

    @Test
    fun `WHEN non-null Long property with no annotated default THEN correct primitive system default is given`() {
        nonNullPrimitiveTest("Long", "0L")
    }

    @Test
    fun `WHEN non-null Long property with annotated default THEN specified default is used`() {
        nonNullPrimitiveWithDefaultTest("Long", "42L")
    }

    @Test
    fun `WHEN nullable Long property with no annotated default THEN builder property defaults to null`() {
        nullablePrimitiveTest("Long")
    }

    @Test
    fun `WHEN nullable Long property with annotated default THEN builder property uses specified default`() {
        nullablePrimitiveWithDefaultTest("Long", "42L")
    }

// endregion

// region Float tests

    @Test
    fun `WHEN non-null Float property with no annotated default THEN correct primitive system default is given`() {
        nonNullPrimitiveTest("Float", "0f")
    }

    @Test
    fun `WHEN non-null Float property with annotated default THEN specified default is used`() {
        nonNullPrimitiveWithDefaultTest("Float", "42.0f")
    }

    @Test
    fun `WHEN nullable Float property with no annotated default THEN builder property defaults to null`() {
        nullablePrimitiveTest("Float")
    }

    @Test
    fun `WHEN nullable Float property with annotated default THEN builder property uses specified default`() {
        nullablePrimitiveWithDefaultTest("Float", "42.0f")
    }

// endregion

// region Double tests

    @Test
    fun `WHEN non-null Double property with no annotated default THEN correct primitive system default is given`() {
        nonNullPrimitiveTest("Double", "0.0")
    }

    @Test
    fun `WHEN non-null Double property with annotated default THEN specified default is used`() {
        nonNullPrimitiveWithDefaultTest("Double", "42.0")
    }

    @Test
    fun `WHEN nullable Double property with no annotated default THEN builder property defaults to null`() {
        nullablePrimitiveTest("Double")
    }

    @Test
    fun `WHEN nullable Double property with annotated default THEN builder property uses specified default`() {
        nullablePrimitiveWithDefaultTest("Double", "42.0")
    }

// endregion

// region Boolean tests

    @Test
    fun `WHEN non-null Boolean property with no annotated default THEN correct primitive system default is given`() {
        nonNullPrimitiveTest("Boolean", "false")
    }

    @Test
    fun `WHEN non-null Boolean property with annotated default THEN specified default is used`() {
        nonNullPrimitiveWithDefaultTest("Boolean", "true")
    }

    @Test
    fun `WHEN nullable Boolean property with no annotated default THEN builder property defaults to null`() {
        nullablePrimitiveTest("Boolean")
    }

    @Test
    fun `WHEN nullable Boolean property with annotated default THEN builder property uses specified default`() {
        nullablePrimitiveWithDefaultTest("Boolean", "true")
    }

// endregion

// region Short tests

    @Test
    fun `WHEN non-null Short property with no annotated default THEN correct primitive system default is given`() {
        nonNullPrimitiveTest("Short", "0")
    }

    @Test
    fun `WHEN non-null Short property with annotated default THEN specified default is used`() {
        nonNullPrimitiveWithDefaultTest("Short", "42")
    }

    @Test
    fun `WHEN nullable Short property with no annotated default THEN builder property defaults to null`() {
        nullablePrimitiveTest("Short")
    }

    @Test
    fun `WHEN nullable Short property with annotated default THEN builder property uses specified default`() {
        nullablePrimitiveWithDefaultTest("Short", "42")
    }

// endregion

// region Byte tests

    @Test
    fun `WHEN non-null Byte property with no annotated default THEN correct primitive system default is given`() {
        nonNullPrimitiveTest("Byte", "0")
    }

    @Test
    fun `WHEN non-null Byte property with annotated default THEN specified default is used`() {
        nonNullPrimitiveWithDefaultTest("Byte", "42")
    }

    @Test
    fun `WHEN nullable Byte property with no annotated default THEN builder property defaults to null`() {
        nullablePrimitiveTest("Byte")
    }

    @Test
    fun `WHEN nullable Byte property with annotated default THEN builder property uses specified default`() {
        nullablePrimitiveWithDefaultTest("Byte", "42")
    }

// endregion

// region Char tests

    @Test
    fun `WHEN non-null Char property with no annotated default THEN correct primitive system default is given`() {
        nonNullPrimitiveTest("Char", "'0'")
    }

    @Test
    fun `WHEN non-null Char property with annotated default THEN specified default is used`() {
        nonNullPrimitiveWithDefaultTest("Char", "'A'")
    }

    @Test
    fun `WHEN nullable Char property with no annotated default THEN builder property defaults to null`() {
        nullablePrimitiveTest("Char")
    }

    @Test
    fun `WHEN nullable Char property with annotated default THEN builder property uses specified default`() {
        nullablePrimitiveWithDefaultTest("Char", "'A'")
    }

// endregion

// region String tests

    @Test
    fun `WHEN non-null String property with no annotated default THEN correct primitive system default is given`() {
        nonNullPrimitiveTest("String", "\"\"")
    }

    @Test
    fun `WHEN non-null String property with annotated default THEN specified default is used`() {
        nonNullPrimitiveWithDefaultTest("String", "\"default\"")
    }

    @Test
    fun `WHEN nullable String property with no annotated default THEN builder property defaults to null`() {
        nullablePrimitiveTest("String")
    }

    @Test
    fun `WHEN nullable String property with annotated default THEN builder property uses specified default`() {
        nullablePrimitiveWithDefaultTest("String", "\"default\"")
    }

// endregion

// region BOILERPLATE

    private fun assertBuilderOutput(
        className: String,
        sourceContent: String,
        expectedOutput: String
    ) {
        val kspCompileResult = compile(
            SourceFile.kotlin(
                "$className.kt",
                sourceContent
            )
        )

        assertEquals(KotlinCompilation.ExitCode.OK, kspCompileResult.result.exitCode)

        val generatedBuilderFile = kspCompileResult.generatedFiles.firstOrNull { it.name == "${className}Builder.kt" }

        assertNotNull(generatedBuilderFile)
        assertEquals(expectedOutput, generatedBuilderFile.readText().trimIndent())
    }

    private fun compile(vararg sourceFiles: SourceFile): KspCompileResult {
        val compilation = prepareCompilation(*sourceFiles)
        val result = compilation.compile()
        return KspCompileResult(
            result,
            findGeneratedFiles(compilation)
        )
    }

    private fun prepareCompilation(vararg sourceFiles: SourceFile): KotlinCompilation =
        KotlinCompilation()
            .apply {
                inheritClassPath = true
                symbolProcessorProviders = listOf(AutoBuilderProcessorProvider())
                sources = sourceFiles.asList()
                verbose = false
                kspIncremental = true
            }

    private fun findGeneratedFiles(compilation: KotlinCompilation): List<File> {
        return compilation.kspSourcesDir
            .walkTopDown()
            .filter { it.isFile }
            .toList()
    }

    /**
     * A data class that contains ksp processed result.
     */
    private data class KspCompileResult(
        val result: KotlinCompilation.Result,
        val generatedFiles: List<File>
    )

    private fun nullablePrimitiveWithDefaultTest(
        type: String,
        value: String
    ) {
        assertBuilderOutput(
            "Foo",

            """
                package io.github.mattshoe.shoebox
    
                import io.github.mattshoe.shoebox.autobuilder.annotations.*
    
                @AutoBuilder
                data class Foo(
                    @Default$type($value)
                    val bar: $type?,
                )
            """,

            """
                package io.github.mattshoe.shoebox.autobuilder
                
                import io.github.mattshoe.shoebox.Foo
                import kotlin.$type
                
                public class FooBuilder {
                    private var bar: $type? = $value
                
                    public fun bar(bar: $type?): FooBuilder {
                        this.bar = bar
                        return this
                    }
                
                    public fun build(): Foo = Foo(
                        bar
                    )
                }
        """.trimIndent()
        )
    }

    private fun nullablePrimitiveTest(
        type: String
    ) {
        assertBuilderOutput(
            "Foo",

            """
                package io.github.mattshoe.shoebox
    
                import io.github.mattshoe.shoebox.autobuilder.annotations.*
    
                @AutoBuilder
                data class Foo(
                    val bar: $type?,
                )
            """,

            """
                package io.github.mattshoe.shoebox.autobuilder
                
                import io.github.mattshoe.shoebox.Foo
                import kotlin.$type
                
                public class FooBuilder {
                    private var bar: $type? = null
                
                    public fun bar(bar: $type?): FooBuilder {
                        this.bar = bar
                        return this
                    }
                
                    public fun build(): Foo = Foo(
                        bar
                    )
                }
        """.trimIndent()
        )
    }

    private fun nonNullPrimitiveWithDefaultTest(
        type: String,
        value: String
    ) {
        assertBuilderOutput(
            "Foo",

            """
                package io.github.mattshoe.shoebox
    
                import io.github.mattshoe.shoebox.autobuilder.annotations.*
    
                @AutoBuilder
                data class Foo(
                    @Default$type($value)
                    val bar: $type,
                )
            """,

            """
                package io.github.mattshoe.shoebox.autobuilder
                
                import io.github.mattshoe.shoebox.Foo
                import kotlin.$type
                
                public class FooBuilder {
                    private var bar: $type = $value
                
                    public fun bar(bar: $type): FooBuilder {
                        this.bar = bar
                        return this
                    }
                
                    public fun build(): Foo = Foo(
                        bar ?: throw IllegalStateException("bar must not be null!")
                    )
                }
        """.trimIndent()
        )
    }

    private fun nonNullPrimitiveTest(
        type: String,
        value: String
    ) {
        assertBuilderOutput(
            "Foo",

            """
                package io.github.mattshoe.shoebox
    
                import io.github.mattshoe.shoebox.autobuilder.annotations.*
    
                @AutoBuilder
                data class Foo(
                    val bar: $type,
                )
            """,

            """
                package io.github.mattshoe.shoebox.autobuilder
                
                import io.github.mattshoe.shoebox.Foo
                import kotlin.$type
                
                public class FooBuilder {
                    private var bar: $type = $value
                
                    public fun bar(bar: $type): FooBuilder {
                        this.bar = bar
                        return this
                    }
                
                    public fun build(): Foo = Foo(
                        bar ?: throw IllegalStateException("bar must not be null!")
                    )
                }
        """.trimIndent()
        )
    }

// endregion
}