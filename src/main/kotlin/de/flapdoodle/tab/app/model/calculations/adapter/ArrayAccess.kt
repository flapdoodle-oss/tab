package de.flapdoodle.tab.app.model.calculations.adapter

import de.flapdoodle.eval.core.EvaluationContext
import de.flapdoodle.eval.core.VariableResolver
import de.flapdoodle.eval.core.evaluables.TypedEvaluable
import de.flapdoodle.eval.core.evaluables.TypedEvaluables
import de.flapdoodle.eval.core.parser.Token
import java.math.BigInteger

object ArrayAccess : TypedEvaluables.Wrapper(TypedEvaluables.builder()
    .addList(TypedEvaluable.of(Any::class.java, List::class.java, BigInteger::class.java, ListAccess()))
    .addList(TypedEvaluable.of(Any::class.java, Map::class.java, String::class.java, PropertyAccess.MapAccess()))
    .build()) {

    class ListAccess : TypedEvaluable.Arg2<List<*>, BigInteger, Any?> {
        override fun evaluate(
            variableResolver: VariableResolver?,
            evaluationContext: EvaluationContext?,
            token: Token?,
            first: List<*>,
            second: BigInteger
        ): Any? {
            return first.get(second.toInt())
        }

    }

}