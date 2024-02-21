package de.flapdoodle.tab.app.model.calculations

interface Calculator {
    fun variables(): Set<Variable>
    fun evaluate(variables: Map<Variable, Any?>): Any?
}