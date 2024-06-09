package de.flapdoodle.tab.model.calculations

import de.flapdoodle.eval.core.Expression
import de.flapdoodle.eval.core.evaluables.Evaluated
import de.flapdoodle.reflection.TypeInfo

interface Formula {
    fun variables(): Set<Variable>
//    fun expression(): String
    fun expression(): Expression
    @Deprecated("use Expression")
    fun change(newFormula: String): Formula
    fun change(newExpression: Expression): Formula
    fun evaluate(values: Map<Variable, Evaluated<out Any>>): Evaluated<out Any>
    fun evaluateType(values: Map<Variable, Evaluated<out Any>>): TypeInfo<out Any>
}