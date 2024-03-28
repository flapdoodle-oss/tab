package de.flapdoodle.tab.app.model.calculations.adapter

import de.flapdoodle.eval.core.Expression
import de.flapdoodle.eval.core.VariableResolver
import de.flapdoodle.tab.app.model.calculations.Formula
import de.flapdoodle.tab.app.model.calculations.Variable

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

    override fun evaluate(values: Map<Variable, Any?>): Any? {
        var resolver = VariableResolver.empty()
        values.forEach { variable, value ->
            resolver = if (value != null) {
                resolver.with(variable.name, value)
            } else {
                resolver.with(variable.name, Null)
            }
        }
        return expression.evaluate(resolver)
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
}