package de.flapdoodle.tab.app.model.calculations.adapter.arithmetic

import de.flapdoodle.eval.core.EvaluationContext
import de.flapdoodle.eval.core.VariableResolver
import de.flapdoodle.eval.core.evaluables.TypedEvaluable
import de.flapdoodle.eval.core.evaluables.TypedEvaluable.Arg2
import de.flapdoodle.eval.core.evaluables.TypedEvaluables
import de.flapdoodle.eval.core.exceptions.EvaluationException
import de.flapdoodle.eval.core.parser.Token
import de.flapdoodle.eval.example.Value
import de.flapdoodle.tab.app.model.calculations.adapter.Evaluables
import java.math.BigDecimal

object Multiply : Evaluables(
    listOf(
            TypedEvaluable.of(
                BigDecimal::class.java,
                BigDecimal::class.java,
                BigDecimal::class.java, Number()
            )
        )
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
            return first.multiply(second, evaluationContext.mathContext())
        }
    }
}