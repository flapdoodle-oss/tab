package de.flapdoodle.tab.app.model.calculations.adapter.arithmetic

import de.flapdoodle.eval.core.evaluables.TypedEvaluable.of
import de.flapdoodle.tab.app.model.calculations.adapter.Evaluables
import de.flapdoodle.tab.app.model.calculations.adapter.Evaluables.Arg2Math

object Multiply : Evaluables(
    of(bigDecimal, bigDecimal, bigDecimal, Arg2Math { first, second, mathContext ->
        first.multiply(second, mathContext)
    }),
    of(bigDecimal, bigDecimal, bigInt, Arg2Math { first, second, mathContext ->
        first.multiply(second.toBigDecimal(), mathContext)
    }),
    of(bigDecimal, bigInt, bigDecimal, Arg2Math { first, second, mathContext ->
        first.toBigDecimal().multiply(second, mathContext)
    }),
    of(bigDecimal, bigDecimal, javaInt, Arg2Math { first, second, mathContext ->
        first.multiply(second.toBigDecimal(), mathContext)
    }),
    of(bigDecimal, javaInt, bigDecimal, Arg2Math { first, second, mathContext ->
        first.toBigDecimal().multiply(second, mathContext)
    }),

    of(bigInt, bigInt, bigInt, Arg2Math { first, second, _ ->
        first.multiply(second)
    }),
    of(bigInt, bigInt, javaInt, Arg2Math { first, second, _ ->
        first.multiply(second.toBigInteger())
    }),
    of(bigInt, javaInt, bigInt, Arg2Math { first, second, _ ->
        first.toBigInteger().multiply(second)
    }),

    of(javaInt, javaInt, javaInt, Arg2Math { first, second, _ ->
        Math.multiplyExact(first, second)
    }),
)