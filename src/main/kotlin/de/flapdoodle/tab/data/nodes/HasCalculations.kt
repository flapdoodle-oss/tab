package de.flapdoodle.tab.data.nodes

import de.flapdoodle.tab.data.NamedColumn
import de.flapdoodle.tab.data.calculations.CalculationMapping

interface HasCalculations {
  fun calculations(): List<CalculationMapping<out Any>>
}