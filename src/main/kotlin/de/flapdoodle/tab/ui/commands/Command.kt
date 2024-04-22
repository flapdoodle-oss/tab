package de.flapdoodle.tab.ui.commands

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