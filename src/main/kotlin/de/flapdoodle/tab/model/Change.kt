package de.flapdoodle.tab.model

import de.flapdoodle.kfx.types.Id

sealed class Change {
    data class AddNode(val node: Node): Change()
    data class RemoveNode(val id: Id<out Node>): Change()
}