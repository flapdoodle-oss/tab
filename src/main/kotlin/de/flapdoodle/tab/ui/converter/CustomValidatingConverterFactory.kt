package de.flapdoodle.tab.ui.converter

import de.flapdoodle.kfx.converters.TypedValidatingConverterFactory
import java.time.Month

object CustomValidatingConverterFactory : TypedValidatingConverterFactory(
    listOf(factory(Month::class, ::MonthConverter))
) {
}