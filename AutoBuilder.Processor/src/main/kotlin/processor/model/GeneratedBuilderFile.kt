package io.github.mattshoe.shoebox.autobuilder.processor.model

import com.squareup.kotlinpoet.FileSpec

data class GeneratedBuilderFile(
    val packageDestination: String,
    val className: String,
    val builderClassName: String,
    val fileSpec: FileSpec
)