package de.flapdoodle.tab.converter

import javafx.util.StringConverter
import javafx.util.converter.BigDecimalStringConverter
import javafx.util.converter.BigIntegerStringConverter
import javafx.util.converter.DefaultStringConverter
import javafx.util.converter.DoubleStringConverter
import javafx.util.converter.FloatStringConverter
import javafx.util.converter.IntegerStringConverter
import javafx.util.converter.LocalDateStringConverter
import javafx.util.converter.LocalDateTimeStringConverter
import javafx.util.converter.LocalTimeStringConverter
import javafx.util.converter.LongStringConverter
import javafx.util.converter.NumberStringConverter
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.reflect.KClass

object Converters {
  fun <S : Any> converterFor(s: KClass<out S>): StringConverter<S> {
    @Suppress("UNCHECKED_CAST")
    return when (s.javaPrimitiveType ?: s) {
      Int::class -> IntegerStringConverter() as StringConverter<S>
      Integer::class -> IntegerStringConverter() as StringConverter<S>
      Integer::class.javaPrimitiveType -> IntegerStringConverter() as StringConverter<S>
      Double::class -> DoubleStringConverter() as StringConverter<S>
      Double::class.javaPrimitiveType -> DoubleStringConverter() as StringConverter<S>
      Float::class -> FloatStringConverter() as StringConverter<S>
      Float::class.javaPrimitiveType -> FloatStringConverter() as StringConverter<S>
      Long::class -> LongStringConverter() as StringConverter<S>
      Long::class.javaPrimitiveType -> LongStringConverter() as StringConverter<S>
      Number::class -> NumberStringConverter() as StringConverter<S>
      BigDecimal::class -> BigDecimalStringConverter() as StringConverter<S>
      BigInteger::class -> BigIntegerStringConverter() as StringConverter<S>
      String::class -> DefaultStringConverter() as StringConverter<S>
      LocalDate::class -> LocalDateStringConverter() as StringConverter<S>
      LocalTime::class -> LocalTimeStringConverter() as StringConverter<S>
      LocalDateTime::class -> LocalDateTimeStringConverter() as StringConverter<S>
//      Boolean::class.javaPrimitiveType -> {
//        (this as TableColumn<T, Boolean?>).useCheckbox(true)
//      }
      else -> throw RuntimeException("makeEditable() is not implemented for specified class type:" + s.qualifiedName)
    }
  }
}