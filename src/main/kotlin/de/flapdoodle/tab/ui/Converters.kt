package de.flapdoodle.tab.ui

import de.flapdoodle.kfx.converters.Converters
import de.flapdoodle.kfx.converters.ValidatingConverter
import de.flapdoodle.reflection.TypeInfo
import java.util.*
import kotlin.reflect.KClass

object Converters {
    fun <T : Any> validatingConverter(type: TypeInfo<T>): ValidatingConverter<T> {
        return Converters.validatingFor(type, Locale.getDefault())
    }

    fun <T : Any> validatingConverter(type: KClass<T>): ValidatingConverter<T> {
        return validatingConverter(TypeInfo.of(type.javaObjectType))
    }
}