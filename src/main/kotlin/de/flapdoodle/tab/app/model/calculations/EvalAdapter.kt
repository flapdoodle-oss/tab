package de.flapdoodle.tab.app.model.calculations

import de.flapdoodle.eval.core.Expression
import de.flapdoodle.eval.example.Defaults

data class EvalAdapter(
    val formula: String,
    val expression: Expression = expressionFactory.parse(formula),
    val variablesWithHash: List<Pair<Variable, Int>> = expression.usedVariablesWithHash()
        .map { Variable(it.key) to it.value }
): Formula {

    private val variables = variablesWithHash.map { it.first }.toCollection(linkedSetOf())

    override fun variables(): Set<Variable> = variables

    fun changeFormula(changed: String): EvalAdapter {
        return if (changed != formula) {
            val changedExpression = expressionFactory.parse(changed)
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
                formula = changed,
                expression = changedExpression,
                variablesWithHash = changedVariables
            )
        } else {
            this
        }
    }

    companion object {
        private val expressionFactory = Defaults.expressionFactory()
    }
}