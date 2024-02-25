package de.flapdoodle.tab.app.model.calculations

interface Formula {
    fun variables(): Set<Variable>
    fun change(newFormula: String): Formula
    fun evaluate(values: Map<Variable, Any?>): Any?
}