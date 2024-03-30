package de.flapdoodle.tab.app.model.calculations.adapter.arithmetic

import de.flapdoodle.eval.core.evaluables.TypedEvaluable.of
import de.flapdoodle.eval.core.evaluables.TypedEvaluable.ofVarArg
import de.flapdoodle.tab.app.model.calculations.adapter.Evaluables
import de.flapdoodle.tab.app.model.calculations.adapter.Evaluables.*
import de.flapdoodle.tab.app.model.calculations.types.IndexMap
import java.math.BigDecimal
import java.math.BigInteger

object Plus : Evaluables(
    // VarArgs
    ofVarArg(bigDecimal, bigDecimal, VarArgMath { list, math ->
        list.fold(BigDecimal.ZERO) { l, r -> l.add(r, math) }
    }),
    ofVarArg(bigInt, bigInt, VarArgMath { list, _ ->
        list.fold(BigInteger.ZERO) { l, r -> l.add(r) }
    }),
    ofVarArg(javaInt, javaInt, VarArgMath { list, _ ->
        list.fold(0) { l, r -> l + r }
    }),

    of(bigDecimal, IndexMap.asParameterWithValueType(BigDecimal::class), ArgMath { argument, mathContext ->
        argument.foldValuesIfNotEmpty(BigDecimal.ZERO) { l, r -> l.add(r, mathContext) }
    }),
    of(bigInt, IndexMap.asParameterWithValueType(BigInteger::class), ArgMath { argument, mathContext ->
        if (argument.values().isNotEmpty()) {
            argument.values().fold(BigInteger.ZERO) { l, r -> l.add(r) }
        } else
            null
    }),
    of(Int::class.java, IndexMap.asParameterWithValueType(Int::class), ArgMath { argument, mathContext ->
        if (argument.values().isNotEmpty()) {
            argument.values().fold(0) { l, r -> l + r }
        } else
            null
    }),
    of(bigDecimal, bigDecimal, bigInt,
        Arg2Math { first, second, math -> first.add(second.toBigDecimal(), math) }),
    of(bigDecimal, bigDecimal, javaInt,
        Arg2Math { first, second, math -> first.add(second.toBigDecimal(), math) }),
    of(bigDecimal, bigInt, bigDecimal,
        Arg2Math { first, second, math -> second.add(first.toBigDecimal(), math) }),
    of(bigDecimal, javaInt, bigDecimal,
        Arg2Math { first, second, math -> second.add(first.toBigDecimal(), math) }),
    of(bigInt, bigInt, javaInt,
        Arg2Math { first, second, math -> first.add(second.toBigInteger()) }),
    of(bigInt, javaInt, bigInt,
        Arg2Math { first, second, math -> first.toBigInteger().add(second) }),
    of(String::class.java, String::class.java, Any::class.java,
        Arg2Math { first, second, math -> first + second }),
    of(String::class.java, Any::class.java, String::class.java,
        Arg2Math { first, second, math -> first.toString() + second })
)
