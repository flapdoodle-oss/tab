package de.flapdoodle.tab.model.calculations.adapter.arithmetic

import de.flapdoodle.eval.core.evaluables.TypedEvaluable.of
import de.flapdoodle.eval.core.evaluables.TypedEvaluable.ofVarArg
import de.flapdoodle.tab.model.calculations.adapter.Evaluables
import de.flapdoodle.tab.model.calculations.adapter.Evaluables.*
import de.flapdoodle.tab.model.calculations.types.IndexMap
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
    ofVarArg(javaDouble, javaDouble, VarArgMath { list, _ ->
        list.fold(0.0) { l, r -> l + r }
    }),
    ofVarArg(javaInt, javaInt, VarArgMath { list, _ ->
        list.fold(0) { l, r -> Math.addExact(l,r) }
    }),
    of(bigDecimal, IndexMap.asParameterWithValueType(bigDecimal), ArgMath { argument, mathContext ->
        argument.foldValuesIfNotEmpty(BigDecimal.ZERO) { l, r -> l.add(r, mathContext) }
    }),
    of(bigInt, IndexMap.asParameterWithValueType(bigInt), ArgMath { argument, _ ->
        argument.foldValuesIfNotEmpty(BigInteger.ZERO) { l, r -> l.add(r) }
    }),
    of(javaDouble, IndexMap.asParameterWithValueType(javaDouble), ArgMath { argument, _ ->
        argument.foldValuesIfNotEmpty(0.0) { l, r -> l + r }
    }),
    of(javaInt, IndexMap.asParameterWithValueType(javaInt), ArgMath { argument, _ ->
        argument.foldValuesIfNotEmpty(0) { l, r -> Math.addExact(l,r) }
    }),
    of(bigDecimal, bigDecimal, bigInt,
        Arg2Math { first, second, math -> first.add(second.toBigDecimal(), math) }),
    of(bigDecimal, bigInt, bigDecimal,
        Arg2Math { first, second, math -> second.add(first.toBigDecimal(), math) }),
    of(bigDecimal, bigDecimal, javaDouble,
        Arg2Math { first, second, math -> first.add(second.toBigDecimal(), math) }),
    of(bigDecimal, javaDouble, bigDecimal,
        Arg2Math { first, second, math -> second.add(first.toBigDecimal(), math) }),
    of(bigDecimal, bigDecimal, javaInt,
        Arg2Math { first, second, math -> first.add(second.toBigDecimal(), math) }),
    of(bigDecimal, javaInt, bigDecimal,
        Arg2Math { first, second, math -> second.add(first.toBigDecimal(), math) }),
    of(bigDecimal, bigInt, javaDouble,
        Arg2Math { first, second, _ -> first.toBigDecimal().add(second.toBigDecimal()) }),
    of(bigDecimal, javaDouble, bigInt,
        Arg2Math { first, second, _ -> first.toBigDecimal().add(second.toBigDecimal()) }),
    of(bigDecimal, javaInt, javaDouble,
        Arg2Math { first, second, _ -> first.toBigDecimal().add(second.toBigDecimal()) }),
    of(bigDecimal, javaDouble, javaInt,
        Arg2Math { first, second, _ -> first.toBigDecimal().add(second.toBigDecimal()) }),
    of(bigInt, bigInt, javaInt,
        Arg2Math { first, second, _ -> first.add(second.toBigInteger()) }),
    of(bigInt, javaInt, bigInt,
        Arg2Math { first, second, _ -> first.toBigInteger().add(second) }),
    of(String::class.java, String::class.java, Any::class.java,
        Arg2Math { first, second, _ -> first + second }),
    of(String::class.java, Any::class.java, String::class.java,
        Arg2Math { first, second, _ -> first.toString() + second })
)
