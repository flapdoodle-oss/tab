package de.flapdoodle.tab.app.model.calculations.adapter.arithmetic

import de.flapdoodle.eval.core.EvaluationContext
import de.flapdoodle.eval.core.VariableResolver
import de.flapdoodle.eval.core.evaluables.TypedEvaluable.Arg2
import de.flapdoodle.eval.core.evaluables.TypedEvaluable.of
import de.flapdoodle.eval.core.evaluables.TypedEvaluables
import de.flapdoodle.eval.core.parser.Token
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext

object Plus : TypedEvaluables.Wrapper(
    TypedEvaluables.builder()
        .addList(
            of(BigDecimal::class.java, BigDecimal::class.java, BigDecimal::class.java,
                Add { first, second, math -> first.add(second, math) })
        )
        .addList(
            of(BigDecimal::class.java, BigDecimal::class.java, BigInteger::class.java,
                Add { first, second, math -> first.add(second.toBigDecimal(), math) })
        )
        .addList(
            of(BigDecimal::class.java, BigDecimal::class.java, Int::class.javaObjectType,
                Add { first, second, math -> first.add(second.toBigDecimal(), math) })
        )
        .addList(
            of(BigDecimal::class.java, BigInteger::class.java, BigDecimal::class.java,
                Add { first, second, math -> second.add(first.toBigDecimal(), math) })
        )
        .addList(
            of(BigDecimal::class.java, Int::class.javaObjectType, BigDecimal::class.java,
                Add { first, second, math -> second.add(first.toBigDecimal(), math) })
        )
        .addList(
            of(BigInteger::class.java, BigInteger::class.java, BigInteger::class.java,
                Add { first, second, math -> first.add(second) })
        )
        .addList(
            of(BigInteger::class.java, BigInteger::class.java, Int::class.javaObjectType,
                Add { first, second, math -> first.add(second.toBigInteger()) })
        )
        .addList(
            of(BigInteger::class.java, Int::class.javaObjectType, BigInteger::class.java,
                Add { first, second, math -> first.toBigInteger().add(second) })
        )
        .addList(
            of(Int::class.javaObjectType, Int::class.javaObjectType, Int::class.javaObjectType,
                Add { first, second, math -> first + second })
        )
        .addList(
            of(String::class.java, String::class.java, Any::class.java,
                Add { first, second, math -> first + second })
        )
        .addList(
            of(String::class.java, Any::class.java, String::class.java,
                Add { first, second, math -> first.toString() + second })
        )
        .build()
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
}