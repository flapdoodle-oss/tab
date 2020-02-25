package de.flapdoodle.tab.data.calculations

import de.flapdoodle.tab.data.values.Values

interface Aggregation<S : Any, D : Any> {
  fun aggregate(src: List<S?>): D?
}