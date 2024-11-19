package de.flapdoodle.tab.model.calculations.adapter.basic

import de.flapdoodle.eval.core.evaluables.Parameter
import de.flapdoodle.eval.core.evaluables.TypedEvaluable
import de.flapdoodle.eval.core.evaluables.TypedEvaluable.of
import de.flapdoodle.eval.core.exceptions.EvaluationException
import de.flapdoodle.eval.core.parser.Token
import de.flapdoodle.tab.model.calculations.adapter.Evaluables
import java.math.BigDecimal
import java.math.RoundingMode

object Round {
    val stringParameter = Parameter.of(String::class.java)

    fun roundingMode(token: Token, mode: String): RoundingMode {
        return when (mode) {
            "ceiling" -> RoundingMode.CEILING
            "down" -> RoundingMode.DOWN
            "up" -> RoundingMode.UP
            "half-down" -> RoundingMode.HALF_DOWN
            "half-up" -> RoundingMode.HALF_UP
            "half-even" -> RoundingMode.HALF_EVEN
            "floor" -> RoundingMode.FLOOR
            else -> {
                throw EvaluationException(token, "unsupported: $mode")
            }
        }
    }

    object Numbers : Evaluables(
        of(bigDecimal,bigDecimalParameter,javaIntParameter, TypedEvaluable.Arg2<BigDecimal, Int, BigDecimal> { _, ctx, token, value, scale ->
            value.setScale(scale, ctx.mathContext().roundingMode)
        }),
        of(bigDecimal,bigDecimalParameter,stringParameter,TypedEvaluable.Arg2<BigDecimal, String, BigDecimal> { _, _, token, value, mode ->
            value.setScale(0, roundingMode(token, mode))
        }),
        of(bigDecimal,bigDecimalParameter,javaIntParameter, stringParameter,TypedEvaluable.Arg3<BigDecimal, Int, String, BigDecimal> { _, _, token, value, scale, mode ->
            value.setScale(scale, roundingMode(token, mode))
        })
    )

}