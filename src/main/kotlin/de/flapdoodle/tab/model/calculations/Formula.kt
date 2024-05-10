package de.flapdoodle.tab.model.calculations

import de.flapdoodle.eval.core.evaluables.Evaluated

interface Formula {
    fun variables(): Set<Variable>
    fun expression(): String
    fun change(newFormula: String): Formula
    fun evaluate(values: Map<Variable, Evaluated<out Any>>): Evaluated<out Any>
}