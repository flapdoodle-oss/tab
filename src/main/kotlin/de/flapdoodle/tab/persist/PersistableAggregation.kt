package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.calculations.Aggregation
import de.flapdoodle.tab.data.calculations.NumberAggregation
import kotlin.reflect.KClass

data class PersistableAggregation(
    val type: Type,
    val numberAggregation: PersistableNumberAggregation? = null
) {

  enum class Type(override val type: KClass<out Aggregation<out Any>>) : TypeClassEnum<Type, Aggregation<out Any>> {
    Number(NumberAggregation::class)
  }

  companion object : PersistableAdapter<Aggregation<out Any>, PersistableAggregation> {
    override fun toPersistable(source: Aggregation<out Any>): PersistableAggregation {
      return when (source) {
        is NumberAggregation -> PersistableAggregation(Type.Number, PersistableNumberAggregation.toPersistable(source))
        else -> throw IllegalArgumentException("not implemented: $source")
      }
    }

    override fun from(context: FromPersistableContext, source: PersistableAggregation): Aggregation<out Any> {
      return return when (source.type) {
        Type.Number -> PersistableNumberAggregation.from(context, requireNotNull(source.numberAggregation) { "numberAggregation not set" })
        else -> throw IllegalArgumentException("not implemented: $source")
      }
    }
  }

  data class PersistableNumberAggregation(
      val type: NumberAggregation.Type
  ) {

    companion object : PersistableAdapter<NumberAggregation, PersistableNumberAggregation> {
      override fun toPersistable(source: NumberAggregation): PersistableNumberAggregation {
        return PersistableNumberAggregation(
            type = source.type
        )
      }

      override fun from(context: FromPersistableContext, source: PersistableNumberAggregation): NumberAggregation {
        return NumberAggregation(
            type = source.type
        )
      }

    }

  }
}