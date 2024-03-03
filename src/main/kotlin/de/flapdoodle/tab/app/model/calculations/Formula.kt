package de.flapdoodle.tab.app.model.calculations

interface Formula {
    fun variables(): Set<Variable>
    fun expression(): String
    fun change(newFormula: String): Formula
    fun evaluate(values: Map<Variable, Any?>): Any?
}