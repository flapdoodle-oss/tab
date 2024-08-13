package de.flapdoodle.tab.ui.views.csv

import de.flapdoodle.kfx.converters.impl.BigDecimalConverter
import de.flapdoodle.kfx.converters.impl.StringConverter
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.ui.resources.Labels
import java.math.BigDecimal
import java.util.*

object ColumnFormats {
    private val all = listOf(
        ColumnFormat(
            name = Labels.text(ColumnFormats::class, "number", "Number"),
            converter = ColumnConverter<BigDecimal>(
                type = TypeInfo.of(BigDecimal::class.java),
                validatingConverter = BigDecimalConverter(Locale.getDefault())
            )
        ),
        ColumnFormat(
            name = Labels.text(ColumnFormats::class, "text", "Text"),
            converter = ColumnConverter<String>(
                type = TypeInfo.of(String::class.java),
                validatingConverter = StringConverter()
            )
        ),
    )

    fun all() = all
}