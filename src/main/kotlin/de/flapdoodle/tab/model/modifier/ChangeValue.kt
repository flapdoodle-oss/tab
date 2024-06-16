package de.flapdoodle.tab.model.modifier

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.data.SingleValueId

data class ChangeValue(
    val id: Id<out Node.Constants>,
    val valueId: SingleValueId,
    val value: Any?
) : ConstantModifier(
    id = id,
    change = { constants -> constants.copy(values = constants.values.set(valueId, value)) }
)