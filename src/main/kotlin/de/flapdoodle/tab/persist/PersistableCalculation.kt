package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.calculations.Calculation
import de.flapdoodle.tab.data.calculations.EvalExCalculationAdapter

data class PersistableCalculation(
    val formula: String
) {

  companion object : PersistableAdapter<Calculation<out Any>, PersistableCalculation> {
    override fun toPersistable(source: Calculation<out Any>): PersistableCalculation {
      require(source is EvalExCalculationAdapter) { "other implementations not supported: $source" }

      return PersistableCalculation(
          formula = source.formula
      )
    }

    override fun from(context: FromPersistableContext, source: PersistableCalculation): Calculation<out Any> {
      return EvalExCalculationAdapter(
          formula = source.formula
      )
    }
  }
}