package de.flapdoodle.tab.ui.events

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Position
import de.flapdoodle.tab.model.Size
import de.flapdoodle.tab.model.calculations.InputSlot
import de.flapdoodle.tab.model.data.DataId
import de.flapdoodle.types.Either

sealed class ModelEvent {
  data class TryToConnect(val node: Id<out de.flapdoodle.tab.model.Node>, val dataOrInput: Either<DataId, Id<InputSlot<*>>>): ModelEvent()

  data class TryToConnectTo(
      val start: Id<out de.flapdoodle.tab.model.Node>,
      val startDataOrInput: Either<DataId, Id<InputSlot<*>>>,
      val end: Id<out de.flapdoodle.tab.model.Node>,
      val endDataOrInput: Either<DataId, Id<InputSlot<*>>>
  ): ModelEvent()

  data class ConnectTo(
      val start: Id<out de.flapdoodle.tab.model.Node>,
      val startDataOrInput: Either<DataId, Id<InputSlot<*>>>,
      val end: Id<out de.flapdoodle.tab.model.Node>,
      val endDataOrInput: Either<DataId, Id<InputSlot<*>>>
  ): ModelEvent()

  data class VertexMoved(
      val node: Id<out de.flapdoodle.tab.model.Node>,
      val position: Position
  ): ModelEvent()

  data class VertexResized(
      val node: Id<out de.flapdoodle.tab.model.Node>,
      val position: Position,
      val size: Size
  ): ModelEvent()
}