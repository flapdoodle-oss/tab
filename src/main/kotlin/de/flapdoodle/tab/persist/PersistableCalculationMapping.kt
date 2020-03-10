package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.calculations.CalculationMapping

data class PersistableCalculationMapping(
    val calculation: PersistableCalculation,
    val column: PersistableNamedColumn
) {

  companion object : ToPersistable<CalculationMapping<out Any>,PersistableCalculationMapping> {
    override fun toPersistable(source: CalculationMapping<out Any>): PersistableCalculationMapping {
      return PersistableCalculationMapping(
          calculation = PersistableCalculation.toPersistable(source.calculation),
          column = PersistableNamedColumn.toPersistable(source.column)
      )
    }
  }
}