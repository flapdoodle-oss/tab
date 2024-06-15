package de.flapdoodle.tab.model

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.calculations.InputSlot
import de.flapdoodle.tab.model.connections.Source
import de.flapdoodle.tab.model.data.DataId
import de.flapdoodle.types.Either

sealed class Change {
    data class AddNode(val node: Node): Change()
    data class RemoveNode(val id: Id<out Node>): Change()

    data class Move(val id: Id<out Node>, val position: Position): Change()
    data class Resize(val id: Id<out Node>, val position: Position, val size: Size): Change()

    data class Connect(
        val startId: Id<out Node>,
        val startDataOrInput: Either<DataId, Id<InputSlot<*>>>,
        val endId: Id<out Node>,
        val endDataOrInput: Either<DataId, Id<InputSlot<*>>>
    ): Change()

    data class Disconnect(
        val endId: Id<out Node>,
        val input: Id<InputSlot<*>>,
        val source: Source
    ): Change()
}