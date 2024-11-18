package de.flapdoodle.tab.model.calculations.adapter

import de.flapdoodle.eval.core.evaluables.Parameter
import java.math.BigDecimal
import java.math.BigInteger

object CommonTypeMappings {
    private val javaIntToBigDecimal: (Int) -> BigDecimal = { it -> BigDecimal.valueOf(it.toLong()) }
    private val javaIntToBigInteger: (Int) -> BigInteger = { it -> BigInteger.valueOf(it.toLong()) }
    private val unmappedBigDecimal: (BigDecimal) -> BigDecimal = { it }
    private val unmappedBigInt: (BigInteger) -> BigInteger = { it }


    val mappings = listOf(
        TypeMapping(Evaluables.javaDoubleNullable, Evaluables.javaIntNullable, Evaluables.bigDecimal, Double::toBigDecimal, javaIntToBigDecimal),
        TypeMapping(Evaluables.javaIntNullable, Evaluables.javaDoubleNullable, Evaluables.bigDecimal, javaIntToBigDecimal, Double::toBigDecimal),

        TypeMapping(Evaluables.bigIntNullable, Evaluables.javaIntNullable, Evaluables.bigInt, unmappedBigInt, javaIntToBigInteger),
        TypeMapping(Evaluables.javaIntNullable, Evaluables.bigIntNullable, Evaluables.bigInt, javaIntToBigInteger, unmappedBigInt),

        TypeMapping(Evaluables.bigDecimalNullable, Evaluables.javaIntNullable, Evaluables.bigDecimal, Evaluables.unmappedBigDecimal, javaIntToBigDecimal),
        TypeMapping(Evaluables.javaIntNullable, Evaluables.bigDecimalNullable, Evaluables.bigDecimal, javaIntToBigDecimal, Evaluables.unmappedBigDecimal),

        TypeMapping(Evaluables.javaDoubleNullable, Evaluables.bigIntNullable, Evaluables.bigDecimal, Double::toBigDecimal, BigInteger::toBigDecimal),
        TypeMapping(Evaluables.bigIntNullable, Evaluables.javaDoubleNullable, Evaluables.bigDecimal, BigInteger::toBigDecimal, Double::toBigDecimal),

        TypeMapping(Evaluables.javaDoubleNullable, Evaluables.bigDecimalNullable, Evaluables.bigDecimal, Double::toBigDecimal, Evaluables.unmappedBigDecimal),
        TypeMapping(Evaluables.bigDecimalNullable, Evaluables.javaDoubleNullable, Evaluables.bigDecimal, Evaluables.unmappedBigDecimal, Double::toBigDecimal),

        TypeMapping(Evaluables.bigIntNullable, Evaluables.bigDecimalNullable, Evaluables.bigDecimal, BigInteger::toBigDecimal, Evaluables.unmappedBigDecimal),
        TypeMapping(Evaluables.bigDecimalNullable, Evaluables.bigIntNullable, Evaluables.bigDecimal, Evaluables.unmappedBigDecimal, BigInteger::toBigDecimal),
    )
}