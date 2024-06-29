package de.flapdoodle.tab.ui.converter

import de.flapdoodle.kfx.converters.ValidatingConverter
import de.flapdoodle.kfx.converters.ValueOrError
import de.flapdoodle.kfx.i18n.I18NEnumStringConverter
import de.flapdoodle.kfx.i18n.I18NTypeStringConverter
import de.flapdoodle.kfx.i18n.ResourceBundleWrapper
import java.time.Month
import java.util.Locale

class MonthConverter(resourceBundle: ResourceBundleWrapper) : ValidatingConverter<Month> {
    private val converter =  I18NEnumStringConverter(resourceBundle, Month::class)
    private val mapping = Month.values().map { it to converter.toString(it) }
    private val byName = mapping.associate { (m,name) -> name to m }
    private val byMonth = mapping.associate { it }

    override fun fromString(value: String?): ValueOrError<Month> {
        return if (!value.isNullOrBlank()) {
            val month: Month? = byName[value]
            if (month!=null) {
                ValueOrError.value(month)
            } else {
                ValueOrError.error(InvalidMonthException(value, byName.keys))
            }
        } else {
            ValueOrError.noValue()
        }
    }

    override fun toString(value: Month): String {
        return byMonth[value] ?: throw IllegalArgumentException("month not found: $value")
    }
}