package de.flapdoodle.tab.model.calculations.adapter.arithmetic

import de.flapdoodle.eval.core.evaluables.TypedEvaluable.of
import de.flapdoodle.tab.model.calculations.adapter.Evaluables
import de.flapdoodle.tab.model.calculations.types.IndexMap
import java.math.BigDecimal
import java.math.BigInteger

object Avg : Evaluables(
    of(bigDecimal, IndexMap.asParameterWithValueType(bigDecimal), ArgMath { argument, mathContext ->
        val sum = argument.foldValuesIfNotEmpty(BigDecimal.ZERO) { l, r -> l.add(r, mathContext) }
        sum?.divide(BigDecimal.valueOf(argument.keys().size.toLong()))
    }),
    of(bigDecimal, IndexMap.asParameterWithValueType(bigInt), ArgMath { argument, _ ->
        val sum = argument.foldValuesIfNotEmpty(BigInteger.ZERO) { l, r -> l.add(r) }
        sum?.toBigDecimal()?.divide(BigDecimal.valueOf(argument.keys().size.toLong()))
    }),
    of(javaDouble, IndexMap.asParameterWithValueType(javaDouble), ArgMath { argument, _ ->
        val sum = argument.foldValuesIfNotEmpty(0.0) { l, r -> l + r }
        if (sum != null) sum / argument.keys().size else null
    }),
    of(bigDecimal, IndexMap.asParameterWithValueType(javaInt), ArgMath { argument, _ ->
        val sum = argument.foldValuesIfNotEmpty(0) { l, r -> Math.addExact(l,r) }
        sum?.toBigDecimal()?.divide(BigDecimal.valueOf(argument.keys().size.toLong()))
    }),

)