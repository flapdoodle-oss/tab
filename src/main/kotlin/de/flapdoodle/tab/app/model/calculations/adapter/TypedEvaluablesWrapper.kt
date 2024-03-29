package de.flapdoodle.tab.app.model.calculations.adapter

import de.flapdoodle.eval.core.evaluables.TypedEvaluables
import java.math.BigDecimal
import java.math.BigInteger

open class TypedEvaluablesWrapper(val delegate: TypedEvaluables) : TypedEvaluables.Wrapper(delegate) {

    companion object {
        val bigDecimal = BigDecimal::class.java
        val bigInt = BigInteger::class.java
        val javaInt = Int::class.javaObjectType
    }
}