package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.values.Values
import java.math.BigDecimal
import kotlin.reflect.KClass

data class PersistableValues(
    val type: VariableOrColumnType,
    val values: List<String?>
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

//    override fun from(context: FromPersistableContext, source: PersistableValues): Values<out Any> {
//      return when (source.type) {
//        VariableOrColumnType.Number -> fromMapped(source, ::BigDecimal)
//        VariableOrColumnType.Text -> fromMapped(source, { it: String -> it})
//      }
//    }

    fun <T: Any> forType(type: KClass<T>): FromPersistable<Values<T>, PersistableValues> {
      return FromPersistable { context, source ->
        require(type == source.type.type) {"type mismatch: $type != ${source.type.type}"}
        @Suppress("UNCHECKED_CAST")
        when (source.type) {
          VariableOrColumnType.Number -> fromMapped(source, ::BigDecimal) as Values<T>
          VariableOrColumnType.Text -> fromMapped(source) { it} as Values<T>
        }
      }
    }

    inline private fun <reified T: Any> fromMapped(source: PersistableValues, map: (String) -> T): Values<T> {
      return Values(
          type = T::class,
          rows = source.values.mapIndexedNotNull { index, value ->
            if (value!=null) {
              index to map(value)
            } else {
              null
            }
          }.toMap()
      )
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
