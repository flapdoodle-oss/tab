package de.flapdoodle.tab.app.model.calculations.adapter.arithmetic

import de.flapdoodle.eval.core.evaluables.TypedEvaluable.of
import de.flapdoodle.tab.app.model.calculations.adapter.Evaluables
import de.flapdoodle.tab.app.model.calculations.adapter.Evaluables.Arg2Math

object Divide : Evaluables(
    of(bigDecimal, bigDecimalParameter, bigDecimalNotZero, Arg2Math { first, second, mathContext ->
        first.divide(second, mathContext)
    }),
    of(bigDecimal, bigDecimalParameter, bigIntNotZero, Arg2Math { first, second, mathContext ->
        first.divide(second.toBigDecimal(), mathContext)
    }),
    of(bigDecimal, bigIntParameter, bigDecimalNotZero, Arg2Math { first, second, mathContext ->
        first.toBigDecimal().divide(second, mathContext)
    }),
    of(bigDecimal, bigDecimalParameter, javaDoubleNotZero, Arg2Math { first, second, mathContext ->
        first.divide(second.toBigDecimal(), mathContext)
    }),
    of(bigDecimal, javaDoubleParameter, bigDecimalNotZero, Arg2Math { first, second, mathContext ->
        first.toBigDecimal().divide(second, mathContext)
    }),
    of(bigDecimal, bigDecimalParameter, javaIntNotZero, Arg2Math { first, second, mathContext ->
        first.divide(second.toBigDecimal(), mathContext)
    }),
    of(bigDecimal, javaIntParameter, bigDecimalNotZero, Arg2Math { first, second, mathContext ->
        first.toBigDecimal().divide(second, mathContext)
    }),

    of(bigDecimal, bigIntParameter, bigIntNotZero, Arg2Math { first, second, mathContext ->
        first.toBigDecimal().divide(second.toBigDecimal(), mathContext)
    }),
    of(bigDecimal, bigIntParameter, javaDoubleNotZero, Arg2Math { first, second, mathContext ->
        first.toBigDecimal().divide(second.toBigDecimal(), mathContext)
    }),
    of(bigDecimal, bigIntParameter, javaIntNotZero, Arg2Math { first, second, mathContext ->
        first.toBigDecimal().divide(second.toBigDecimal(), mathContext)
    }),
    of(bigDecimal, javaIntParameter, bigIntNotZero, Arg2Math { first, second, mathContext ->
        first.toBigDecimal().divide(second.toBigDecimal(), mathContext)
    }),
    of(bigDecimal, javaDoubleParameter, bigIntNotZero, Arg2Math { first, second, mathContext ->
        first.toBigDecimal().divide(second.toBigDecimal(), mathContext)
    }),

    of(bigDecimal, javaDoubleParameter, javaDoubleNotZero, Arg2Math { first, second, mathContext ->
        first.toBigDecimal().divide(second.toBigDecimal(), mathContext)
    }),
    of(bigDecimal, javaDoubleParameter, javaIntNotZero, Arg2Math { first, second, mathContext ->
        first.toBigDecimal().divide(second.toBigDecimal(), mathContext)
    }),
    of(bigDecimal, javaIntParameter, javaDoubleNotZero, Arg2Math { first, second, mathContext ->
        first.toBigDecimal().divide(second.toBigDecimal(), mathContext)
    }),

    of(bigDecimal, javaIntParameter, javaIntNotZero, Arg2Math { first, second, mathContext ->
        first.toBigDecimal().divide(second.toBigDecimal(), mathContext)
    }),
) {

}