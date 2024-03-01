package de.flapdoodle.tab.app.model.change

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.data.SingleValueId

sealed class ModelChange {
    data class ChangeValue(val id: Id<out Node>, val valueId: SingleValueId, val value: Any?): ModelChange()
}