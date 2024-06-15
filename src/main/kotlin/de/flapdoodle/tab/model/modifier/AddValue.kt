package de.flapdoodle.tab.model.modifier

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.data.SingleValue

data class AddValue(
    val id: Id<out Node.Constants>,
    val value: SingleValue<out Any>
) : ConstantModifier(
    id = id,
    change = { constants -> constants.addValue(value) }
)