package de.flapdoodle.tab.app.model.calculations

interface Formula {
    fun variables(): Set<Variable>
}