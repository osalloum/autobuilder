package io.github.mattshoe.shoebox.autobuilder.io.github.mattshoe.shoebox.autobuilder.processor.property

data class PropertyDefinition(
    val value: String,
    val imports: List<ImportStatement>
)
