package de.flapdoodle.tab.app.model.calculations.adapter.arithmetic

import de.flapdoodle.eval.core.EvaluationContext
import de.flapdoodle.eval.core.VariableResolver
import de.flapdoodle.eval.core.evaluables.TypedEvaluable
import de.flapdoodle.eval.core.evaluables.TypedEvaluable.*
import de.flapdoodle.eval.core.exceptions.EvaluationException
import de.flapdoodle.eval.core.parser.Token
import de.flapdoodle.tab.app.model.calculations.adapter.Evaluables
import java.math.BigDecimal
import java.math.BigInteger

object Minus : Evaluables(
    of(bigDecimal, bigDecimal, bigDecimal, Arg2Math { first, second, mathContext ->
        first.subtract(second, mathContext)
    }),
    of(bigDecimal, bigDecimal, bigInt, Arg2Math { first, second, mathContext ->
        first.subtract(second.toBigDecimal(), mathContext)
    }),
    of(bigDecimal, bigInt, bigDecimal, Arg2Math { first, second, mathContext ->
        first.toBigDecimal().subtract(second, mathContext)
    }),
    of(bigDecimal, bigDecimal, javaDouble, Arg2Math { first, second, mathContext ->
        first.subtract(second.toBigDecimal(), mathContext)
    }),
    of(bigDecimal, javaDouble, bigDecimal, Arg2Math { first, second, mathContext ->
        first.toBigDecimal().subtract(second, mathContext)
    }),
    of(bigDecimal, bigDecimal, javaInt, Arg2Math { first, second, mathContext ->
        first.subtract(second.toBigDecimal(), mathContext)
    }),
    of(bigDecimal, javaInt, bigDecimal, Arg2Math { first, second, mathContext ->
        first.toBigDecimal().subtract(second, mathContext)
    }),

    of(bigInt, bigInt, bigInt, Arg2Math { first, second, _ ->
        first.subtract(second)
    }),
    of(bigDecimal, bigInt, javaDouble, Arg2Math { first, second, _ ->
        first.toBigDecimal().subtract(second.toBigDecimal())
    }),
    of(bigDecimal, javaDouble, bigInt, Arg2Math { first, second, _ ->
        first.toBigDecimal().subtract(second.toBigDecimal())
    }),
    of(bigInt, bigInt, javaInt, Arg2Math { first, second, _ ->
        first.subtract(second.toBigInteger())
    }),
    of(bigInt, javaInt, bigInt, Arg2Math { first, second, _ ->
        first.toBigInteger().subtract(second)
    }),
    of(bigDecimal, javaInt, javaDouble, Arg2Math { first, second, _ ->
        first.toBigDecimal().subtract(second.toBigDecimal())
    }),
    of(bigDecimal, javaDouble, javaInt, Arg2Math { first, second, _ ->
        first.toBigDecimal().subtract(second.toBigDecimal())
    }),
    of(javaDouble, javaDouble, javaDouble, Arg2Math { first, second, _ ->
        first - second
    }),
    of(javaInt, javaInt, javaInt, Arg2Math { first, second, _ ->
        Math.subtractExact(first, second)
    }),

    of(bigDecimal, bigDecimal, ArgMath { first, mathContext -> first.negate(mathContext) })
) {

//    class DateTimeDuration : Arg2<DateTimeValue, DurationValue, DateTimeValue> {
//        @Throws(EvaluationException::class)
//        override fun evaluate(
//            variableResolver: VariableResolver,
//            evaluationContext: EvaluationContext,
//            token: Token,
//            first: DateTimeValue,
//            second: DurationValue
//        ): DateTimeValue {
//            return Value.of(first.wrapped().minus(second.wrapped()))
//        }
//    }
//
//    class DateTimeDateTime : Arg2<DateTimeValue, DateTimeValue, DurationValue> {
//        @Throws(EvaluationException::class)
//        override fun evaluate(
//            variableResolver: VariableResolver,
//            evaluationContext: EvaluationContext,
//            token: Token,
//            first: DateTimeValue,
//            second: DateTimeValue
//        ): DurationValue {
//            return Value.of(Duration.ofMillis(first.wrapped().toEpochMilli() - second.wrapped().toEpochMilli()))
//        }
//    }
//
//    class Durations : Arg2<DurationValue, DurationValue, DurationValue> {
//        @Throws(EvaluationException::class)
//        override fun evaluate(
//            variableResolver: VariableResolver,
//            evaluationContext: EvaluationContext,
//            token: Token,
//            first: DurationValue,
//            second: DurationValue
//        ): DurationValue {
//            return Value.of(first.wrapped().minus(second.wrapped()))
//        }
//    }
//
//    class DateTimeNumber : Arg2<DateTimeValue, BigDecimal, DateTimeValue> {
//        @Throws(EvaluationException::class)
//        override fun evaluate(
//            variableResolver: VariableResolver,
//            evaluationContext: EvaluationContext,
//            token: Token,
//            first: DateTimeValue,
//            second: BigDecimal
//        ): DateTimeValue {
//            return first.minus(Duration.ofMillis(second.toLong()))
//        }
//    }

}
