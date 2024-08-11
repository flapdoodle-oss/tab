package de.flapdoodle.tab.ui.views.csv

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.ui.resources.Labels
import java.math.BigDecimal

object ColumnFormats {

    fun all() = listOf(
        ColumnFormat(
            name = Labels.text(ColumnFormats::class, "number","Number"),
            converter = ColumnConverter<BigDecimal>(
                type = TypeInfo.of(BigDecimal::class.java),
                converter = { value, format -> BigDecimal(value) },
            )
        ),
        ColumnFormat(
            name = Labels.text(ColumnFormats::class, "text","Text"),
            converter = ColumnConverter<String>(
                type = TypeInfo.of(String::class.java),
                converter = { value, format -> value },
            )
        ),
    )
}