package de.flapdoodle.tab.ui.views.csv

import de.flapdoodle.reflection.TypeInfo

object CsvCellConverters {
    val all = listOf<CsvCellConverter<*>>(
        CsvCellConverter(
            name = "Text",
            type = TypeInfo.of(String::class.java),
            conversion = { it, _ -> it}
        )
    )
}