package de.flapdoodle.tab.model.calculations.adapter

import de.flapdoodle.eval.core.EvaluationContext
import de.flapdoodle.eval.core.VariableResolver
import de.flapdoodle.eval.core.evaluables.*
import de.flapdoodle.eval.core.parser.Token
import de.flapdoodle.tab.model.calculations.types.IndexMap
import java.math.BigInteger

// TODO ArrayAccess muss wohl anders funktionieren..
//  da müsste man den ReturnType direkt aus dem Wert bestimmen
//  und nicht vorher festlegen
object ArrayAccess : TypedEvaluables.Wrapper(TypedEvaluables.builder()
    .addList(TypedEvaluable.of(Any::class.java, List::class.java, BigInteger::class.java, ListAccess()))
    .addList(TypedEvaluable.of(Any::class.java, Map::class.java, String::class.java, PropertyAccess.MapAccess()))
//    .addList(TypedEvaluable.of(Any::class.java, IndexMap::class.java, Any::class.java, IndexMapInterpolatedAccess()))
    .addList(IndexMapInterpolatedAdapter())
    .build()) {

    class ListAccess : TypedEvaluable.Arg2<List<*>, BigInteger, Any?> {
        override fun evaluate(
            variableResolver: VariableResolver,
            evaluationContext: EvaluationContext,
            token: Token,
            first: List<*>,
            second: BigInteger
        ): Any? {
            return first[second.toInt()]
        }

    }

    class IndexMapInterpolatedAdapter : TypedEvaluable<Any> {
        private val signature = Signature.of(Any::class.java, Parameter.of(IndexMap::class.java), Parameter.of(Any::class.java))

        override fun signature(): Signature<Any> = signature

        override fun evaluate(
            variableResolver: VariableResolver?,
            evaluationContext: EvaluationContext?,
            token: Token?,
            arguments: MutableList<out Evaluated<*>>?
        ): Evaluated<Any> {
            signature.validateArguments(arguments)
            val unwrapped = Evaluated.unwrap(arguments)
            val indexMap = unwrapped[0] as IndexMap<*, *>
            return interpolated(indexMap, unwrapped[1]) as Evaluated<Any>
        }

    }

    private fun <K: Comparable<K>, V: Any> interpolated(first: IndexMap<K, V>, second: Any?): Evaluated<V> {
        require(first.indexType().isInstance(second)) {"index $second does not match ${first.indexType()}"}
        val index = second as K
//        println("interpolate $first with $index")
        val evaluated = first.interpolator().interpolated(index)
//        println("interpolate $first with $index -> $evaluated")
        return evaluated
    }
}