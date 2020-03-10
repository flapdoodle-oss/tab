package de.flapdoodle.tab.persist

import java.math.BigDecimal
import kotlin.reflect.KClass

enum class VariableOrColumnType(
    override val type: KClass<out Any>
): TypeClassEnum<VariableOrColumnType, Any> {
  Number(BigDecimal::class),
  Text(String::class)
}