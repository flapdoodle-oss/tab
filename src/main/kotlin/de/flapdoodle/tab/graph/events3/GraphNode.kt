package de.flapdoodle.tab.graph.events3

import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import tornadofx.*

fun <T: UIComponent> EventTarget.uiGroup(parentComponent: T, initialChildren: Iterable<Node>? = null, op: GraphNode.UIComponentGroup<T>.() -> Unit = {}) =
    opcr<GraphNode.UIComponentGroup<T>>(this, GraphNode.UIComponentGroup(parentComponent).apply { if (initialChildren != null) children.addAll(initialChildren) }, op)


class GraphNode(
    private val x: Number = 0.0,
    private val y: Number = 0.0
) : Fragment() {
  override val root = uiGroup(this) {
    rectangle(x = x, y = y) {
      style {
        fill = Color.BLUE
        width = 20.0
        height = 20.0
      }

      onMouseEntered = EventHandler {
        println("entered.. try sending event")
        fire(EnterNodeEvent(this@GraphNode))
      }
    }
  }

  data class EnterNodeEvent(val parent: GraphNode) : FXEvent()

  class UIComponentGroup<T: UIComponent>(
      private val parentComponent: T
  ) : Group() {

    fun parentComponent() = parentComponent
  }
}