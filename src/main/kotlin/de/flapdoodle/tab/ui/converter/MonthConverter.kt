package de.flapdoodle.tab.ui.converter

import de.flapdoodle.kfx.converters.ValidatingConverter
import de.flapdoodle.kfx.converters.ValueOrError
import java.time.Month
import java.util.Locale

class MonthConverter(val locale: Locale) : ValidatingConverter<Month> {
    private val byName = Month.values().associateBy { it.name }

    override fun fromString(value: String?): ValueOrError<Month> {
        return if (!value.isNullOrBlank()) {
            val month: Month? = byName.get(value)
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
        return value.name
    }
}