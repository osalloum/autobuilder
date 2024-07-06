package io.github.mattshoe.shoebox.autobuilder.processor.model

data class PropertyData(
    val value: String,
    val imports: List<ImportStatement>
)
