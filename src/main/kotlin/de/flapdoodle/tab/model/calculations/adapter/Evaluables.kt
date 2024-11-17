package de.flapdoodle.tab.model.calculations.adapter

import de.flapdoodle.eval.core.EvaluationContext
import de.flapdoodle.eval.core.VariableResolver
import de.flapdoodle.eval.core.evaluables.*
import de.flapdoodle.eval.core.exceptions.EvaluableException
import de.flapdoodle.eval.core.parser.Token
import de.flapdoodle.eval.core.validation.ParameterValidator
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.types.Either
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.util.*

open class Evaluables(
    val list: List<TypedEvaluable<out Any>>
) : TypedEvaluableByArguments, TypedEvaluableByNumberOfArguments {
    constructor(vararg all: TypedEvaluable<out Any>) : this(listOf(*all))

    override fun find(values: MutableList<out Evaluated<*>>): Either<TypedEvaluable<*>, EvaluableException> {
        return TypedEvaluableByArguments.find(list, values)
    }

    override fun findType(valueTypes: MutableList<out TypeInfo<*>>): Either<TypedEvaluable<*>, EvaluableException> {
        return TypedEvaluableByArguments.findType(list, valueTypes)
    }

    override fun filterByNumberOfArguments(numberOfArguments: Int): Optional<out TypedEvaluableByArguments> {
        val filtered = TypedEvaluableByNumberOfArguments.filterByNumberOfArguments(list, numberOfArguments)
        return if (filtered.isNotEmpty()) Optional.of(Evaluables(filtered)) else Optional.empty()
    }

    companion object {
        val javaAny = Any::class.javaObjectType
        val javaAnyParameterLazy = Parameter.lazyWith(javaAny)

        val boolean = Boolean::class.java
        val booleanParameter = Parameter.of(boolean)
        val javaBoolean = Boolean::class.javaObjectType

        val bigDecimal = BigDecimal::class.java
        val bigInt = BigInteger::class.java
        val javaInt = Int::class.javaObjectType
        val javaDouble = Double::class.javaObjectType

        val bigDecimalParameter = Parameter.of(BigDecimal::class.java)!!
        val bigIntParameter = Parameter.of(BigInteger::class.java)!!
        val javaIntParameter = Parameter.of(javaInt)!!
        val javaDoubleParameter = Parameter.of(javaDouble)!!

        val bigDecimalNullable = Parameter.nullableWith(BigDecimal::class.java)!! as Parameter<BigDecimal?>
        val bigIntNullable = Parameter.nullableWith(BigInteger::class.java)!! as Parameter<BigInteger?>
        val javaIntNullable = Parameter.nullableWith(javaInt)!!  as Parameter<Int?>
        val javaDoubleNullable = Parameter.nullableWith(javaDouble)!!  as Parameter<Double?>

        val bigDecimalNotZero = bigDecimalParameter.withValidators(isNot(BigDecimal.ZERO,"division by zero"))!!
        val bigIntNotZero = bigIntParameter.withValidators(isNot(BigInteger.ZERO,"division by zero"))!!
        val javaIntNotZero = javaIntParameter.withValidators(isNot(0,"division by zero"))!!
        val javaDoubleNotZero = javaDoubleParameter.withValidators(isNot(0.0,"division by zero"))!!

        fun <T : Any> isNot(match: T, text: String): ParameterValidator<T> {
            return ParameterValidator { value ->
                if (match == value) {
                    Optional.of(EvaluableException.of(text, match, value))
                } else
                    Optional.empty()
            }
        }
    }

    fun interface ArgMath<A : Any, T : Any> : TypedEvaluable.Arg1<A, T> {
        override fun evaluate(
            variableResolver: VariableResolver,
            evaluationContext: EvaluationContext,
            token: Token?,
            first: A
        ): T? {
            return evaluate(first, evaluationContext.mathContext())
        }

        fun evaluate(first: A, mathContext: MathContext): T?
    }

    fun interface Arg2Math<A : Any, B : Any, T : Any> : TypedEvaluable.Arg2<A, B, T> {
        override fun evaluate(
            variableResolver: VariableResolver,
            evaluationContext: EvaluationContext,
            token: Token?,
            first: A,
            second: B
        ): T? {
            return evaluate(first, second, evaluationContext.mathContext())
        }

        fun evaluate(first: A, second: B, mathContext: MathContext): T?
    }

    fun interface Arg2NullableMath<A : Any?, B : Any?, T : Any?> : TypedEvaluable.Arg2<A, B, T> {
        override fun evaluate(
            variableResolver: VariableResolver,
            evaluationContext: EvaluationContext,
            token: Token?,
            first: A?,
            second: B?
        ): T? {
            return evaluate(first, second, evaluationContext.mathContext())
        }

        fun evaluate(first: A?, second: B?, mathContext: MathContext): T?
    }

    fun interface VarArgMath<A : Any, T : Any> : TypedEvaluable.VarArg1<A, T> {
        override fun evaluate(
            variableResolver: VariableResolver,
            evaluationContext: EvaluationContext,
            token: Token?,
            arguments: MutableList<A>
        ): T? {
            return evaluate(arguments, evaluationContext.mathContext())
        }

        fun evaluate(list: List<A>, mathContext: MathContext): T?
    }
}