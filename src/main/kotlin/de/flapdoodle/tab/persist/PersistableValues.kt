package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.values.Values
import java.math.BigDecimal

data class PersistableValues(
    val type: VariableOrColumnType,
    val values: List<Any?>
) {

  companion object : ToPersistable<Values<out Any>, PersistableValues> {
    override fun toPersistable(source: Values<out Any>): PersistableValues {
      @Suppress("UNCHECKED_CAST")
      return when (source.type) {
        BigDecimal::class -> bigDecimalToPersistable(source as Values<BigDecimal>)
        String::class -> stringToPersistable(source as Values<String>)
        else -> throw IllegalArgumentException("not implemented: $source")
      }
    }

    private fun stringToPersistable(source: Values<String>): PersistableValues {
      return PersistableValues(
          type = TypeClassEnum.typeOf(VariableOrColumnType::class, source.type),
          values = (0 until source.size()).map { source[it] }
      )
    }

    private fun bigDecimalToPersistable(source: Values<BigDecimal>): PersistableValues {
      return PersistableValues(
          type = TypeClassEnum.typeOf(VariableOrColumnType::class, source.type),
          values = (0 until source.size()).map { source[it]?.toString() }
      )
    }
  }
}
