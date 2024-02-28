package de.flapdoodle.tab.app.ui.commands

import de.flapdoodle.kfx.controls.graphmodeleditor.types.VertexId
import javafx.geometry.Point2D

sealed class Command {
  class Abort() : Command()
  class AskForPosition(
    val onSuccess: (Point2D) -> Unit
  ) : Command()
//  class FindVertex<V>(
//    val vertex: VertexId<V>,
//    val onSuccess: () -> Unit
//  ) : Command<V>()
}