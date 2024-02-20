package de.flapdoodle.tab.app.model

import de.flapdoodle.kfx.types.Id

data class Inputs(val inputs: List<Input> = emptyList()) {
    init {
        val collisions = inputs.groupBy { it.id }
            .filter { it.value.size > 1 }
            .keys
        require(collisions.isEmpty()) { "same id used more than once: $collisions"}
    }
    private val valueIdMap by lazy { inputs.associateBy { it.id } }

    fun addInput(input: Input): Inputs {
        return copy(inputs = inputs + input)
    }

    fun input(id: Id<Input>): Input {
        return requireNotNull(valueIdMap[id]) { "value $id not found" }
    }
}