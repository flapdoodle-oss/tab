package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.values.ValueContainer
import java.math.BigDecimal
import kotlin.reflect.KClass

data class PersistableValues(
    val type: VariableOrColumnType,
    val values: List<String?>
) {

  companion object : ToPersistable<ValueContainer<out Any>, PersistableValues> {
    override fun toPersistable(source: ValueContainer<out Any>): PersistableValues {
      @Suppress("UNCHECKED_CAST")
      return when (source.type) {
        BigDecimal::class -> bigDecimalToPersistable(source as ValueContainer<BigDecimal>)
        String::class -> stringToPersistable(source as ValueContainer<String>)
        else -> throw IllegalArgumentException("not implemented: $source")
      }
    }

//    override fun from(context: FromPersistableContext, source: PersistableValues): Values<out Any> {
//      return when (source.type) {
//        VariableOrColumnType.Number -> fromMapped(source, ::BigDecimal)
//        VariableOrColumnType.Text -> fromMapped(source, { it: String -> it})
//      }
//    }

    fun <T: Any> forType(type: KClass<T>): FromPersistable<ValueContainer<T>, PersistableValues> {
      return FromPersistable { context, source ->
        require(type == source.type.type) {"type mismatch: $type != ${source.type.type}"}
        @Suppress("UNCHECKED_CAST")
        when (source.type) {
          VariableOrColumnType.Number -> fromMapped(source, ::BigDecimal) as ValueContainer<T>
          VariableOrColumnType.Text -> fromMapped(source) { it} as ValueContainer<T>
        }
      }
    }

    inline private fun <reified T: Any> fromMapped(source: PersistableValues, map: (String) -> T): ValueContainer<T> {
      return ValueContainer(
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

    private fun stringToPersistable(source: ValueContainer<String>): PersistableValues {
      return PersistableValues(
          type = TypeClassEnum.typeOf(VariableOrColumnType::class, source.type),
          values = source.asList()
      )
    }

    private fun bigDecimalToPersistable(source: ValueContainer<BigDecimal>): PersistableValues {
      return PersistableValues(
          type = TypeClassEnum.typeOf(VariableOrColumnType::class, source.type),
          values = source.asList().map { it?.toString() }
      )
    }
  }
}
