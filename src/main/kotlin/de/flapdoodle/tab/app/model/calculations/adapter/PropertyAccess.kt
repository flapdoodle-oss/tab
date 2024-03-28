package de.flapdoodle.tab.app.model.calculations.adapter

import de.flapdoodle.eval.core.EvaluationContext
import de.flapdoodle.eval.core.VariableResolver
import de.flapdoodle.eval.core.evaluables.TypedEvaluable
import de.flapdoodle.eval.core.evaluables.TypedEvaluable.Arg2
import de.flapdoodle.eval.core.evaluables.TypedEvaluables
import de.flapdoodle.eval.core.exceptions.EvaluationException
import de.flapdoodle.eval.core.parser.Token

object PropertyAccess : TypedEvaluables.Wrapper(TypedEvaluables.builder()
    .addList(TypedEvaluable.of(Any::class.java, Map::class.java, String::class.java, MapAccess()))
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
}