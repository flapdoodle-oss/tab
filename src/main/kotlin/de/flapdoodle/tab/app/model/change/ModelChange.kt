package de.flapdoodle.tab.app.model.change

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.data.SingleValueId

sealed class ModelChange {
    sealed class ConstantsChange(open val id: Id<out Node.Constants>) : ModelChange()
    data class ChangeValue(override val id: Id<out Node.Constants>, val valueId: SingleValueId, val value: Any?): ConstantsChange(id)
}