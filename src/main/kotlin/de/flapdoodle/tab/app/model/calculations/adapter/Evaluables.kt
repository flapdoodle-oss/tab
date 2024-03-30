package de.flapdoodle.tab.app.model.calculations.adapter

import de.flapdoodle.eval.core.evaluables.TypedEvaluable
import de.flapdoodle.eval.core.evaluables.TypedEvaluableByArguments
import de.flapdoodle.eval.core.evaluables.TypedEvaluableByNumberOfArguments
import de.flapdoodle.eval.core.exceptions.EvaluableException
import de.flapdoodle.types.Either
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

open class Evaluables(
    val list: List<TypedEvaluable<out Any>>
) : TypedEvaluableByArguments, TypedEvaluableByNumberOfArguments {
    constructor(vararg all: TypedEvaluable<out Any>) : this(listOf(*all))

    override fun find(values: MutableList<*>): Either<TypedEvaluable<*>, EvaluableException> {
        return TypedEvaluableByArguments.find(list, values)
    }

    override fun filterByNumberOfArguments(numberOfArguments: Int): Optional<out TypedEvaluableByArguments> {
        val filtered = TypedEvaluableByNumberOfArguments.filterByNumberOfArguments(list, numberOfArguments)
        return if (filtered.isNotEmpty()) Optional.of(Evaluables(filtered)) else Optional.empty()
    }

    companion object {
        val bigDecimal = BigDecimal::class.java
        val bigInt = BigInteger::class.java
        val javaInt = Int::class.javaObjectType
    }
}