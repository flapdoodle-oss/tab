package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.NamedColumn
import de.flapdoodle.tab.data.calculations.Aggregation
import de.flapdoodle.tab.data.calculations.AggregationMapping
import java.math.BigDecimal

data class PersistableAggregationMapping(
    val aggregation: PersistableAggregation,
    val column: PersistableNamedColumn
) {

  companion object : PersistableAdapter<AggregationMapping<out Any>,PersistableAggregationMapping> {
    override fun toPersistable(source: AggregationMapping<out Any>): PersistableAggregationMapping {
      return PersistableAggregationMapping(
          aggregation = PersistableAggregation.toPersistable(source.aggregation),
          column = PersistableNamedColumn.toPersistable(source.column)
      )
    }

    override fun from(context: FromPersistableContext, source: PersistableAggregationMapping): AggregationMapping<out Any> {
      return fromCasted(context, source)
    }

    @Suppress("UNCHECKED_CAST")
    private fun fromCasted(context: FromPersistableContext, source: PersistableAggregationMapping): AggregationMapping<BigDecimal> {
      val aggregation = PersistableAggregation.from(context, source.aggregation) as Aggregation<BigDecimal>
      val column = PersistableNamedColumn.from(context, source.column)
      require(column.id.type==BigDecimal::class) {"column type missmatch: $column"}
      return AggregationMapping(
          aggregation = aggregation,
          column = column as NamedColumn<BigDecimal>
      )
    }
  }
}