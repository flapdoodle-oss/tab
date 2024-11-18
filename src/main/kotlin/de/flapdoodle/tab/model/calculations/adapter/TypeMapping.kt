package de.flapdoodle.tab.model.calculations.adapter

import de.flapdoodle.eval.core.evaluables.Parameter

data class TypeMapping<L, R, D: Comparable<D>>(
    val left: Parameter<L?>,
    val right: Parameter<R?>,
    val destination: Class<D>,
    val mapLeft: (L) -> D,
    val mapRight: (R) -> D
)