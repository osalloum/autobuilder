# AutoBuilder

`AutoBuilder` is a Kotlin Symbol Processing (KSP) library designed to automatically generate builder classes for your data classes. It simplifies the creation of complex objects and supports default values for both primitive and non-primitive properties.

## Features

- **Automatic Builder Generation**: Automatically generates builder classes for annotated data classes.
- **Support for Primitive Types**: Handles all Kotlin primitive types, including nullable versions.
- **Default Values**: Supports default values specified through annotations or in the constructor.
- **Non-Primitive Properties**: Supports non-primitive properties with either a no-arg constructor or a `@Default` annotation for custom constructor arguments.

## Known Limitations

- Default values specified in the constructor are not currently supported due to KSP limitations. This feature will be implemented once support is available.

<br>
<br>

## Usage

### Annotations

- `@AutoBuilder`: Annotate your data class with `@AutoBuilder` to generate a builder for that class.
- `@DefaultInt`, `@DefaultString`, `@DefaultXX` etc.: Use these annotations to specify default values for primitive properties.
- `@Default`: Use this annotation to specify default values for non-primitive properties using constructor arguments.

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
    @Default(args = ["42", "\"bar\""])
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
    @Default(args = ["SomeObject(42)"], imports = [ "io.github.mattshoe.shoebox.SomeObject"])
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

## Installation

Add the following dependencies to your `build.gradle.kts` file:

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    ksp("io.github.mattshoe.shoebox.autobuilder:AutoBuilder.Processor:1.0.0")
    implementation("io.github.mattshoe.shoebox.autobuilder:AutoBuilder.Annotations:1.0.0")
}
```

## Contributing

Contributions are welcome! Please open an issue or submit a pull request on GitHub.


## Acknowledgements

This project uses the following libraries:
- [KotlinPoet](https://github.com/square/kotlinpoet)
- [KSP](https://github.com/google/ksp)
