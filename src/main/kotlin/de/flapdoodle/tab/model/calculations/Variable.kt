package de.flapdoodle.tab.model.calculations

import de.flapdoodle.kfx.types.Id

data class Variable(
    val name: String,
    val id: Id<Variable> = Id.nextId(Variable::class)
) {
    val isColumnReference = name.startsWith('#')
}