package de.flapdoodle.tab.app.model.calculations

import de.flapdoodle.kfx.types.Key

data class VariableId(
    val id: Int = Key.Companion.nextId(VariableId::class)
)