package de.flapdoodle.tab.app.ui.events

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.calculations.InputSlot
import de.flapdoodle.tab.app.model.data.DataId
import de.flapdoodle.types.Either

sealed class ModelEvent {
  data class TryToConnect(val node: Id<out Node>, val dataOrInput: Either<DataId, Id<InputSlot<*>>>): ModelEvent()

  data class TryToConnectTo(
    val start: Id<out Node>,
    val startDataOrInput: Either<DataId, Id<InputSlot<*>>>,
    val end: Id<out Node>,
    val endDataOrInput: Either<DataId, Id<InputSlot<*>>>
  ): ModelEvent()

  data class ConnectTo(
    val start: Id<out Node>,
    val startDataOrInput: Either<DataId, Id<InputSlot<*>>>,
    val end: Id<out Node>,
    val endDataOrInput: Either<DataId, Id<InputSlot<*>>>
  ): ModelEvent()
}