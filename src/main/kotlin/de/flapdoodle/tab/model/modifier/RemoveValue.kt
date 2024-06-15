package de.flapdoodle.tab.model.modifier

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.data.ColumnId
import de.flapdoodle.tab.model.data.SingleValueId

data class RemoveValue(
    val id: Id<out Node.Constants>,
    val valueId: SingleValueId
) : ConstantModifier(
    id = id,
    change = { table -> table.copy(values = table.values.remove(valueId)) }
)