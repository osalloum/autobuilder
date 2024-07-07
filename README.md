# AutoBuilder

`AutoBuilder` is a Kotlin Symbol Processing (KSP) library designed to automatically generate builder classes for your data classes. 
It simplifies the creation of complex objects and supports default values for both primitive and non-primitive properties.

## Features

- **Automatic Builder Generation**: Automatically generates builder classes for annotated data classes.
- **Support for Primitive Properties**: Handles all Kotlin primitive types, including nullable versions.
- **Support for Complex Object Properties**: Supports non-primitive properties via either a no-arg constructor or the `@DefaultConstructor` annotation to specify constructor arguments.
- **Custom Default Values**: Supports custom default values specified through annotations.

## Known Limitations

- Defaulted constructor values are not currently respected due to KSP limitations. 
  - This feature will be implemented once KSP support is available.
  - https://github.com/google/ksp/issues/1868

<br>

## Usage

### Annotations

- `@AutoBuilder`
  - Annotate your data class with `@AutoBuilder` to generate a builder for that class.
- `@DefaultInt`, `@DefaultString`, `@DefaultBoolean`, ...
  - Use these annotations to specify default values for primitive properties.
- `@DefaultConstructor`
  - Use this to specify default arguments for constructors. 
  - Optional if the target property's type has a no-arg constructor or is nullable.
  - If a property requires a constructor, then you **MUST** use the `@DefaultConstructor` annotation
  - The first argument is the textual representation of all arguments to be passed to the constructor
    - `@DefaultConstructor(args=["42", "false", "\"something\""])`
  - The second argument allows you to specify any imports that are required in order to construct an object:
    - `@DefaultConstructor(args=["FooBar(42)"], imports=["com.foo.bar.FooBar"])`

### Property Default Values
#### Evaluated in order of priority:

- **`@Default` Annotated Properties:**
  - Always takes priority over automatic defaults
- **Nullable Properties:**
  - Defaulted to `null` 
- **Primitive Properties** 
  - Byte: `0`
  - Short: `0`
  - Int: `0`
  - Long: `0L`
  - Float: `0.0f`
  - Double: `0.0`
  - Char: `'\u0000'` (the null character)
  - Boolean: `false`
  - String: `""`
- **Non-Primitive Properties:**
  - Defaults to constructor args specified by `@DefaultConstructor` if annotated
  - Defaults to `null` if property is nullable
  - Defaults to No-Arg constructor if available


### Examples

#### Data Class with Primitive Properties

```kotlin
package io.github.mattshoe.shoebox

import io.github.mattshoe.shoebox.autobuilder.annotations.*

@AutoBuilder
data class SimpleCase(
    val intProperty: Int,
    val stringProperty: String
)
```

#### Data Class with Defaulted Primitive Properties

```kotlin
package io.github.mattshoe.shoebox

import io.github.mattshoe.shoebox.autobuilder.annotations.*

@AutoBuilder
data class SimpleCaseWithDefaults(
    @DefaultInt(42)
    val intProperty: Int,
    @DefaultString("default")
    val stringProperty: String
)
```

#### Data Class with Non-Primitive with No-Arg Constructor 

```kotlin
package io.github.mattshoe.shoebox

import io.github.mattshoe.shoebox.autobuilder.annotations.*

@AutoBuilder
data class Foo(
    val customObject: CustomObject
)

data class SomeObject(
    val arg1: Int = 42
)
```

#### Data Class with Non-Primitive Properties which Requires Constructor Args

```kotlin
package io.github.mattshoe.shoebox

import io.github.mattshoe.shoebox.autobuilder.annotations.*

@AutoBuilder
data class Foo(
    @DefaultConstructor(args = ["42", "\"bar\""])
    val customObject: SomeObject
)

data class SomeObject(
    val foo: Int,
    val bar: String
)
```

#### Data Class with Non-Primitive Property Requiring Direct Instantiation

```kotlin
package io.github.mattshoe.shoebox

import io.github.mattshoe.shoebox.autobuilder.annotations.*

@AutoBuilder
data class Foo(
    @DefaultConstructor(args = ["SomeObject(42)"], imports = [ "io.github.mattshoe.shoebox.SomeObject"])
    val customObject: SomeObject
)

data class SomeObject(
    val foo: Int
)
```

### Generated Builder

For the `SimpleCaseWithDefaults` class, the following builder will be generated:

```kotlin
package io.github.mattshoe.shoebox.autobuilder

import io.github.mattshoe.shoebox.SimpleCaseWithDefaults
import kotlin.Int
import kotlin.String

public class SimpleCaseWithDefaultsBuilder {
    private var intProperty: Int = 42
    private var stringProperty: String = "default"

    public fun intProperty(intProperty: Int): SimpleCaseWithDefaultsBuilder {
        this.intProperty = intProperty
        return this
    }

    public fun stringProperty(stringProperty: String): SimpleCaseWithDefaultsBuilder {
        this.stringProperty = stringProperty
        return this
    }

    public fun build(): ExamplePrimitives = SimpleCaseWithDefaults(
        intProperty ?: throw IllegalStateException("intProperty must not be null!"),
        stringProperty ?: throw IllegalStateException("stringProperty must not be null!")
    )
}
```

<br>

## Installation

Add the following dependencies to your `build.gradle.kts` file:

```kotlin
plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp") version "2.0.0-1.0.21" // Use your judgment on version here
}

repositories {
    mavenCentral()
}

dependencies {
    ksp("io.github.mattshoe.shoebox.autobuilder:AutoBuilder.Processor:1.1.0")
    compileOnly("io.github.mattshoe.shoebox.autobuilder:AutoBuilder.Annotations:1.1.0")
}
```

<br>

## Contributing

Contributions are always welcome!<br>Feel free to open an issue or submit a pull request on GitHub!


## Acknowledgements

This project uses the following libraries:
- [KotlinPoet](https://github.com/square/kotlinpoet)
- [KSP](https://github.com/google/ksp)
