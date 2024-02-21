package de.flapdoodle.tab.app.model.calculations

data class Variable(
    val name: String,
    val id: VariableId = VariableId()
)