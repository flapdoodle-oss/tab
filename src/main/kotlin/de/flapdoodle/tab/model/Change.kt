package de.flapdoodle.tab.model

import de.flapdoodle.kfx.types.Id

sealed class Change {
    data class AddNode(val node: Node): Change()
    data class RemoveNode(val id: Id<out Node>): Change()

    data class Move(val id: Id<out Node>, val position: Position): Change()
    data class Resize(val id: Id<out Node>, val position: Position, val size: Size): Change()
}