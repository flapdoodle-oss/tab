package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.extensions.fire
import de.flapdoodle.tab.graph.events.IsMarker
import de.flapdoodle.tab.graph.events.MappedMouseEvent
import de.flapdoodle.tab.graph.events.MouseEventHandler
import de.flapdoodle.tab.graph.events.MouseEventHandlerResolver
import de.flapdoodle.tab.graph.nodes.connections.Out
import de.flapdoodle.tab.graph.nodes.connections.VariableInput
import de.flapdoodle.tab.graph.nodes.renderer.events.ConnectEvent
import de.flapdoodle.tab.graph.nodes.renderer.events.ExplainEvent
import de.flapdoodle.tab.graph.nodes.renderer.events.ModelEvent
import javafx.geometry.Point2D
import javafx.scene.input.MouseButton

object ConnectNodesHandler {

  class OnInput(
      val input: VariableInput<out Any>
  ) : MouseEventHandler {
    var dragStarted: Point2D? = null
    var exited: Boolean = false

    override fun onEvent(mouseEvent: MappedMouseEvent, marker: IsMarker?): MouseEventHandler? {
//      println("touched $input with $mouseEvent, marker: $marker")

      when (mouseEvent) {
        is MappedMouseEvent.Click -> {
          if (mouseEvent.button()==MouseButton.PRIMARY) {
            dragStarted = mouseEvent.coord
            ConnectEvent.startConnectTo(input, mouseEvent.coord).fire()
          }
        }
        is MappedMouseEvent.DragDetected -> {
          mouseEvent.startFullDrag()
        }
        is MappedMouseEvent.DragEnter -> {
          if (marker is Out<out Any>) {
            ExplainEvent.columnselected(marker.columnId).fire()
          }
        }
        is MappedMouseEvent.Drag -> {
          ConnectEvent.connectTo(input, mouseEvent.coord, source = null).fire()
        }
        is MappedMouseEvent.DragExit -> {
          if (marker is Out<out Any>) {
            ExplainEvent.noColumnSelected().fire()
          }
        }
        is MappedMouseEvent.DragRelease -> {
          if (marker is Out<out Any>) {
            when (marker) {
              is Out.ColumnValues<*> -> {
                ModelEvent.connect(input, marker)?.fire()
              }
            }
          }
          dragStarted = null
          exited = true
        }
        is MappedMouseEvent.Exit -> {
          exited = dragStarted == null
        }
      }

      return if (dragStarted == null && exited) {
        ConnectEvent.stop().fire()
        null
      } else
        this
    }
  }


  class OnOutput(
      val output: Out.ColumnValues<out Any>
  ) : MouseEventHandler {
    var exited: Boolean = false
    var dragStarted: Point2D? = null

    override fun onEvent(mouseEvent: MappedMouseEvent, marker: IsMarker?): MouseEventHandler? {
      println("touched input: $output")
      when (mouseEvent) {
        is MappedMouseEvent.Enter -> {
          exited = false
          ExplainEvent.columnselected(output.columnId).fire()
        }
        is MappedMouseEvent.Click -> {
          dragStarted = mouseEvent.coord
          ConnectEvent.startConnectFrom(output.columnId, mouseEvent.coord).fire()
        }
        is MappedMouseEvent.DragDetected -> {
          mouseEvent.startFullDrag()
        }
        is MappedMouseEvent.DragEnter -> {
          if (marker is VariableInput<out Any>) {
//            ExplainEvent.columnselected(marker.columnId).fire()
          }
        }
        is MappedMouseEvent.Drag -> {
          ConnectEvent.connectFrom(output.columnId, mouseEvent.coord, dest = null).fire()
        }
        is MappedMouseEvent.DragExit -> {
          if (marker is Out<out Any>) {
            ExplainEvent.noColumnSelected().fire()
          }
        }
        is MappedMouseEvent.DragRelease -> {
          if (marker is VariableInput<out Any>) {
            ModelEvent.connect(marker, output)?.fire()
          }
          dragStarted = null
          exited = true
        }
        is MappedMouseEvent.Exit -> {
          exited = dragStarted == null
          ExplainEvent.noColumnSelected().fire()
        }
      }
      return if (dragStarted == null && exited) {
        ConnectEvent.stop().fire()
        null
      } else
        this
    }
  }

  val inputResolver = MouseEventHandlerResolver.forType(::OnInput)
  val outputResolver = MouseEventHandlerResolver.forType(::OnOutput)

}