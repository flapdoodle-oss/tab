package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.NamedColumn
import de.flapdoodle.tab.data.calculations.Calculation
import de.flapdoodle.tab.data.calculations.CalculationMapping
import java.math.BigDecimal

data class PersistableCalculationMapping(
    val calculation: PersistableCalculation,
    val column: PersistableNamedColumn
) {

  companion object : PersistableAdapter<CalculationMapping<out Any>,PersistableCalculationMapping> {
    override fun toPersistable(source: CalculationMapping<out Any>): PersistableCalculationMapping {
      return PersistableCalculationMapping(
          calculation = PersistableCalculation.toPersistable(source.calculation),
          column = PersistableNamedColumn.toPersistable(source.column)
      )
    }

    override fun from(context: FromPersistableContext, source: PersistableCalculationMapping): CalculationMapping<out Any> {
      return fromCasted(context, source)
    }

    @Suppress("UNCHECKED_CAST")
    private fun fromCasted(context: FromPersistableContext, source: PersistableCalculationMapping): CalculationMapping<BigDecimal> {
      val calculation = PersistableCalculation.from(context, source.calculation) as Calculation<BigDecimal>
      val column = PersistableNamedColumn.from(context, source.column)
      require(column.id.type==BigDecimal::class) {"column type missmatch: $column"}
      return CalculationMapping(
          calculation = calculation,
          column = column as NamedColumn<BigDecimal>
      )
    }
  }
}