package de.flapdoodle.tab.model.calculations.adapter

import de.flapdoodle.eval.core.EvaluationContext
import de.flapdoodle.eval.core.VariableResolver
import de.flapdoodle.eval.core.evaluables.TypedEvaluable
import de.flapdoodle.eval.core.evaluables.TypedEvaluables
import de.flapdoodle.eval.core.parser.Token
import de.flapdoodle.tab.model.calculations.types.IndexMap
import java.math.BigInteger

object ArrayAccess : TypedEvaluables.Wrapper(TypedEvaluables.builder()
    .addList(TypedEvaluable.of(Any::class.java, List::class.java, BigInteger::class.java, ListAccess()))
    .addList(TypedEvaluable.of(Any::class.java, Map::class.java, String::class.java, PropertyAccess.MapAccess()))
    .addList(TypedEvaluable.of(Any::class.java, IndexMap::class.java, Any::class.java, IndexMapInterpolatedAccess()))
    .build()) {

    class ListAccess : TypedEvaluable.Arg2<List<*>, BigInteger, Any?> {
        override fun evaluate(
            variableResolver: VariableResolver,
            evaluationContext: EvaluationContext,
            token: Token,
            first: List<*>,
            second: BigInteger
        ): Any? {
            return first.get(second.toInt())
        }

    }

    class IndexMapInterpolatedAccess : TypedEvaluable.Arg2<IndexMap<*,*>, Any, Any?> {
        override fun evaluate(
            variableResolver: VariableResolver,
            evaluationContext: EvaluationContext,
            token: Token,
            first: IndexMap<*, *>,
            second: Any?
        ): Any? {
            return interpolated(first, second)
        }

    }

    private fun <K: Comparable<K>, V: Any> interpolated(first: IndexMap<K, V>, second: Any?): Any? {
        require(first.indexType().isInstance(second)) {"index $second does not match ${first.indexType()}"}
        val index = second as K
        val evaluated = first.interpolator().interpolated(index)
        return if (evaluated.isNull) null else evaluated.wrapped()
    }

}