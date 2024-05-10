package de.flapdoodle.tab.ui

import de.flapdoodle.kfx.converters.Converters
import de.flapdoodle.kfx.converters.ValidatingConverter
import de.flapdoodle.reflection.TypeInfo
import java.util.*
import kotlin.reflect.KClass

object Converters {

    private val localeConverterMap: Map<TypeInfo<out Any>, ValidatingConverter<out Any>> =
        Converters.validatingConverters(Locale.getDefault()).associate { (clazz, converter) ->
            TypeInfo.of(clazz) to converter
        }

    fun <T : Any> validatingConverter(type: TypeInfo<T>): ValidatingConverter<T> {
        val converter = localeConverterMap[type] ?: throw RuntimeException("converter not found: $type")
        @Suppress("UNCHECKED_CAST")
        return converter as ValidatingConverter<T>
    }

    fun <T : Any> validatingConverter(type: KClass<T>): ValidatingConverter<T> {
        return validatingConverter(TypeInfo.of(type.javaObjectType))
    }
}