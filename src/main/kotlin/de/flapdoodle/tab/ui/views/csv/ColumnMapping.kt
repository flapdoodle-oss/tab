package de.flapdoodle.tab.ui.views.csv

import de.flapdoodle.kfx.types.Id

data class ColumnMapping(
    val index: Int,
    val name: String,
    val converter: CsvCellConverter<out Any>,
    val id: Id<ColumnMapping> = Id.nextId(ColumnMapping::class),
)