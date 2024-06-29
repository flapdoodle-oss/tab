package de.flapdoodle.tab.ui

import de.flapdoodle.kfx.converters.DefaultValidatingConverterFactory
import de.flapdoodle.kfx.converters.ValidatingConverter
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.ui.converter.CustomValidatingConverterFactory
import java.util.*
import kotlin.reflect.KClass

object Converters {
    val defaultValidatorFactory = CustomValidatingConverterFactory.or(DefaultValidatingConverterFactory)

    fun <S : Any> validatingConverter(typeInfo: TypeInfo<out S>, locale: Locale = Locale.getDefault()): ValidatingConverter<S> {
        return defaultValidatorFactory.converter(typeInfo, locale)
    }

    fun <T : Any> validatingConverter(type: KClass<T>): ValidatingConverter<T> {
        return validatingConverter(TypeInfo.of(type.javaObjectType))
    }
}