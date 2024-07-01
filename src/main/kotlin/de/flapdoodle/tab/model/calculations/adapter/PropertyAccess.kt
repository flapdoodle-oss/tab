package de.flapdoodle.tab.model.calculations.adapter

import de.flapdoodle.eval.core.EvaluationContext
import de.flapdoodle.eval.core.VariableResolver
import de.flapdoodle.eval.core.evaluables.TypedEvaluable
import de.flapdoodle.eval.core.evaluables.TypedEvaluable.Arg2
import de.flapdoodle.eval.core.evaluables.TypedEvaluables
import de.flapdoodle.eval.core.exceptions.EvaluationException
import de.flapdoodle.eval.core.parser.Token
import de.flapdoodle.tab.model.calculations.adapter.index.IndexTypedEvaluableAdapter
import java.time.LocalDate

object PropertyAccess : TypedEvaluables.Wrapper(TypedEvaluables.builder()
    .addList(TypedEvaluable.of(Any::class.java, Map::class.java, String::class.java, MapAccess()))
    .addList(TypedEvaluable.of(Any::class.java, LocalDate::class.java, String::class.java, LocalDateAccess()))
    .addList(IndexTypedEvaluableAdapter)
    .build()) {

    class MapAccess : Arg2<Map<*,*>, String, Any?> {
        @Throws(EvaluationException::class)
        override fun evaluate(
            variableResolver: VariableResolver,
            evaluationContext: EvaluationContext,
            token: Token,
            first: Map<*,*>,
            second: String
        ): Any? {
            if (!first.containsKey(second)) {
                throw EvaluationException(
                    token, String.format("Field '%s' not found in structure", second)
                )
            }

            return first[second]
        }
    }

    class LocalDateAccess : Arg2<LocalDate, String, Any?> {
        override fun evaluate(
            variableResolver: VariableResolver,
            evaluationContext: EvaluationContext,
            token: Token,
            first: LocalDate,
            property: String
        ): Any? {
            return when (property) {
                "day" -> first.dayOfMonth
                "month" -> first.month
                "year" -> first.year
                else -> throw EvaluationException(token,"unknown property: $property")
            }
        }

    }
}