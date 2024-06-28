package de.flapdoodle.tab.ui

import de.flapdoodle.kfx.converters.Converters
import de.flapdoodle.kfx.converters.TypedValidatingConverterFactory
import de.flapdoodle.kfx.converters.ValidatingConverter
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.ui.converter.MonthConverter
import java.time.Month
import java.util.*
import kotlin.reflect.KClass

object Converters {

    private val converters = listOf(
        TypedValidatingConverterFactory(TypeInfo.of(Month::class.javaObjectType), ::MonthConverter)
    )

    fun <S : Any> validatingConverter(typeInfo: TypeInfo<out S>, locale: Locale = Locale.getDefault()): ValidatingConverter<S> {
        return converters.firstOrNull { it.typeInfo == typeInfo }?.factory?.invoke(locale) as ValidatingConverter<S>?
            ?: Converters.validatingFor(typeInfo, locale)
    }

    fun <T : Any> validatingConverter(type: KClass<T>): ValidatingConverter<T> {
        return validatingConverter(TypeInfo.of(type.javaObjectType))
    }
}