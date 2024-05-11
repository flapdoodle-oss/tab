package de.flapdoodle.tab.model.calculations.adapter

import de.flapdoodle.eval.core.Expression
import de.flapdoodle.eval.core.VariableResolver
import de.flapdoodle.eval.core.VariableTypeResolver
import de.flapdoodle.eval.core.evaluables.Evaluated
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.calculations.Formula
import de.flapdoodle.tab.model.calculations.Variable

data class EvalFormulaAdapter(
    private val formula: String,
    private val expression: Expression = Eval.parse(formula),
    private val variablesWithHash: List<Pair<Variable, Int>> = expression.usedVariablesWithHash()
        .map { Variable(it.key) to it.value }
): Formula {

    private val variables = variablesWithHash.map { it.first }.toCollection(linkedSetOf())

    override fun expression() = formula
    override fun variables(): Set<Variable> = variables
    override fun toString(): String {
        return "EvalFormulaAdapter(formula=$formula, variablesWithHash=$variablesWithHash)"
    }

    override fun evaluate(values: Map<Variable, Evaluated<out Any>>): Evaluated<out Any> {
        var resolver = VariableResolver.empty()
        values.forEach { variable, value ->
            resolver = resolver.with(variable.name, value)
        }
        return expression.evaluate(resolver)
    }

    override fun evaluateType(values: Map<Variable, Evaluated<out Any>>): TypeInfo<out Any> {
        var resolver = VariableTypeResolver.empty()
        values.forEach { variable, value ->
            resolver = resolver.with(variable.name, value.type())
        }
        return expression.evaluateType(resolver)
    }


    override fun change(newFormula: String): EvalFormulaAdapter {
        return if (newFormula != formula) {
            val changedExpression = Eval.parse(newFormula)
            val byId = variablesWithHash.associateBy { it.second }
            val byName = variablesWithHash.associateBy { it.first.name }
            val changedVariables = changedExpression.usedVariablesWithHash().map {
                val old = byId[it.value]?.first
                if (old!=null) {
                    if (it.key != old.name) {
                        old.copy(name = it.key) to it.value
                    } else {
                        old to it.value
                    }
                } else {
                    // different hash
                    val sameName = byName[it.key]
                    if (sameName!=null) {
                        sameName.first to it.value
                    } else {
                        Variable(it.key) to it.value
                    }
                }
            }
            copy(
                formula = newFormula,
                expression = changedExpression,
                variablesWithHash = changedVariables
            )
        } else {
            this
        }
    }

    // exclude expression from hash/equals
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EvalFormulaAdapter

        if (formula != other.formula) return false
        if (variablesWithHash != other.variablesWithHash) return false
        if (variables != other.variables) return false

        return true
    }

    override fun hashCode(): Int {
        var result = formula.hashCode()
        result = 31 * result + variablesWithHash.hashCode()
        result = 31 * result + variables.hashCode()
        return result
    }


}