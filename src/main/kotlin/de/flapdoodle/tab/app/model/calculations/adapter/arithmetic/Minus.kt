package de.flapdoodle.tab.app.model.calculations.adapter.arithmetic

import de.flapdoodle.eval.core.EvaluationContext
import de.flapdoodle.eval.core.VariableResolver
import de.flapdoodle.eval.core.evaluables.TypedEvaluable
import de.flapdoodle.eval.core.evaluables.TypedEvaluable.Arg1
import de.flapdoodle.eval.core.evaluables.TypedEvaluable.Arg2
import de.flapdoodle.eval.core.evaluables.TypedEvaluables
import de.flapdoodle.eval.core.exceptions.EvaluationException
import de.flapdoodle.eval.core.parser.Token
import de.flapdoodle.eval.example.Value
import de.flapdoodle.eval.example.Value.DateTimeValue
import de.flapdoodle.eval.example.Value.DurationValue
import java.math.BigDecimal
import java.time.Duration

object Minus : TypedEvaluables.Wrapper(
    TypedEvaluables.builder()
        .addList(TypedEvaluable.of(BigDecimal::class.java, BigDecimal::class.java, BigDecimal::class.java, Number()))
//        .addList(
//            TypedEvaluable.of(
//                DateTimeValue::class.java,
//                DateTimeValue::class.java,
//                DurationValue::class.java, DateTimeDuration()
//            )
//        )
//        .addList(
//            TypedEvaluable.of(
//                DurationValue::class.java,
//                DateTimeValue::class.java,
//                DateTimeValue::class.java, DateTimeDateTime()
//            )
//        )
//        .addList(
//            TypedEvaluable.of(
//                DurationValue::class.java,
//                DurationValue::class.java,
//                DurationValue::class.java, Durations()
//            )
//        )
//        .addList(
//            TypedEvaluable.of(
//                DateTimeValue::class.java,
//                DateTimeValue::class.java,
//                BigDecimal::class.java, DateTimeNumber()
//            )
//        )
        .addList(TypedEvaluable.of(BigDecimal::class.java, BigDecimal::class.java, Negate()))
        .build()
) {
    class Number :
        Arg2<BigDecimal, BigDecimal, BigDecimal> {
        @Throws(EvaluationException::class)
        override fun evaluate(
            variableResolver: VariableResolver,
            evaluationContext: EvaluationContext,
            token: Token,
            first: BigDecimal,
            second: BigDecimal
        ): BigDecimal {
            return first.subtract(second, evaluationContext.mathContext())
        }
    }

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

    class Negate : Arg1<BigDecimal, BigDecimal> {
        @Throws(EvaluationException::class)
        override fun evaluate(
            variableResolver: VariableResolver,
            evaluationContext: EvaluationContext,
            token: Token,
            first: BigDecimal
        ): BigDecimal {
            return first.negate(evaluationContext.mathContext())
        }
    }
}
