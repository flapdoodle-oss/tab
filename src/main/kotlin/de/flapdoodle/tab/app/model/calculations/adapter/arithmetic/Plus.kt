package de.flapdoodle.tab.app.model.calculations.adapter.arithmetic

import de.flapdoodle.eval.core.EvaluationContext
import de.flapdoodle.eval.core.VariableResolver
import de.flapdoodle.eval.core.evaluables.TypedEvaluable.*
import de.flapdoodle.eval.core.parser.Token
import de.flapdoodle.tab.app.model.calculations.adapter.Evaluables
import de.flapdoodle.tab.app.model.calculations.types.IndexMap
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext

object Plus : Evaluables(
    // VarArgs
    ofVarArg(bigDecimal, bigDecimal, AddVararg { list, math ->
        list.fold(BigDecimal.ZERO) { l, r -> l.add(r, math) }
    }),
    ofVarArg(bigInt, bigInt, AddVararg { list, _ ->
        list.fold(BigInteger.ZERO) { l, r -> l.add(r) }
    }),
    ofVarArg(javaInt, javaInt, AddVararg { list, _ ->
        list.fold(0) { l, r -> l + r }
    }),

    of(
        bigDecimal, IndexMap.asParameterWithValueType(BigDecimal::class)
    ) { _, evaluationContext, _, argument ->
        if (argument.values().isNotEmpty()) {
            argument.values().fold(BigDecimal.ZERO) { l, r -> l.add(r, evaluationContext.mathContext()) }
        } else
            null
    },
    of(bigInt, IndexMap.asParameterWithValueType(BigInteger::class)) { _, _, _, argument ->
        if (argument.values().isNotEmpty()) {
            argument.values().fold(BigInteger.ZERO) { l, r -> l.add(r) }
        } else
            null
    },
    of(Int::class.java, IndexMap.asParameterWithValueType(Int::class)) { _, _, _, argument ->
        if (argument.values().isNotEmpty()) {
            argument.values().fold(0) { l, r -> l + r }
        } else
            null
    },
    of(bigDecimal, bigDecimal, bigInt,
        Add { first, second, math -> first.add(second.toBigDecimal(), math) }),
    of(bigDecimal, bigDecimal, javaInt,
        Add { first, second, math -> first.add(second.toBigDecimal(), math) }),
    of(bigDecimal, bigInt, bigDecimal,
        Add { first, second, math -> second.add(first.toBigDecimal(), math) }),
    of(bigDecimal, javaInt, bigDecimal,
        Add { first, second, math -> second.add(first.toBigDecimal(), math) }),
    of(bigInt, bigInt, javaInt,
        Add { first, second, math -> first.add(second.toBigInteger()) }),
    of(bigInt, javaInt, bigInt,
        Add { first, second, math -> first.toBigInteger().add(second) }),
    of(String::class.java, String::class.java, Any::class.java,
        Add { first, second, math -> first + second }),
    of(String::class.java, Any::class.java, String::class.java,
        Add { first, second, math -> first.toString() + second })
) {

    class Add<A : Any, B : Any, T : Any>(
        val add: (A, B, MathContext) -> T
    ) : Arg2<A, B, T> {
        override fun evaluate(
            variableResolver: VariableResolver,
            evaluationContext: EvaluationContext,
            token: Token,
            first: A,
            second: B
        ): T {
            return add(first, second, evaluationContext.mathContext())
        }
    }

    class AddVararg<A : Any, T : Any>(
        val add: (List<A>, MathContext) -> T
    ) : VarArg1<A, T> {
        override fun evaluate(
            variableResolver: VariableResolver,
            evaluationContext: EvaluationContext,
            token: Token?,
            arguments: MutableList<A>
        ): T? {
            return if (arguments.isNotEmpty()) {
                add(arguments, evaluationContext.mathContext())
            } else
                null
        }
    }

}
