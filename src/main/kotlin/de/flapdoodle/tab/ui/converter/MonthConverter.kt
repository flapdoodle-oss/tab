package de.flapdoodle.tab.ui.converter

import de.flapdoodle.kfx.converters.ValidatingConverter
import de.flapdoodle.kfx.converters.ValueOrError
import java.time.Month
import java.util.Locale

class MonthConverter(val locale: Locale) : ValidatingConverter<Month> {
    override fun fromString(value: String?): ValueOrError<Month> {
        return if (!value.isNullOrBlank()) {
            ValueOrError.value(Month.valueOf(value))
        } else {
            ValueOrError.noValue()
        }
    }

    override fun toString(value: Month): String {
        return value.name
    }
}