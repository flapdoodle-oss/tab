package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.nodes.ColumnConnection
import de.flapdoodle.tab.graph.events.IsMarker
import de.flapdoodle.tab.graph.events.MappedMouseEvent
import de.flapdoodle.tab.graph.events.MouseEventHandler
import de.flapdoodle.tab.graph.events.MouseEventHandlerResolver
import de.flapdoodle.tab.graph.nodes.connections.Out
import de.flapdoodle.tab.graph.nodes.connections.VariableInput
import de.flapdoodle.tab.graph.nodes.renderer.events.ConnectEvent
import de.flapdoodle.tab.graph.nodes.renderer.events.ModelEvent
import javafx.geometry.Point2D
import tornadofx.*

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
          dragStarted = mouseEvent.coord
          val event = ConnectEvent.startConnectTo(input)
          println("fire event: $event")
          FX.eventbus.fire(event)
        }
        is MappedMouseEvent.DragDetected -> {
          mouseEvent.startFullDrag()
        }
        is MappedMouseEvent.DragEnter -> {
          if (marker is Out) {
            println("enter: could connect $input to $marker")
          }
        }
        is MappedMouseEvent.DragRelease -> {
          if (marker is Out) {
            println("released: could connect $input to $marker")
            when (marker) {
              is Out.ColumnValues<*> -> {
                connectIfPossible(input, marker)
              }
            }
            //if (input.variable.type==marker)
          }
          dragStarted = null
          exited = true
        }
//        is MappedMouseEvent.Drag -> {
//          dragStarted?.let { it + mouseEvent.delta }?.apply {
//            //        println("should move ${moveMarker.parent} by ${mouseEvent.delta}")
//            //moveMarker.parent.moveTo(this.x, this.y)
//          }
//          if (marker != null) {
//            println("dragged $input to $marker")
//          }
//          if (marker is Out) {
//            println("could connect $input to $marker")
//          }
//        }
//        is MappedMouseEvent.Release -> dragStarted = null
////        is MappedMouseEvent.Enter -> {
////          if (marker is Out) {
////            println("could connect $input to $marker")
////          }
////        }
        is MappedMouseEvent.Exit -> {
          exited = dragStarted == null
        }
      }
//      println("end $input with $dragStarted")

      return if (dragStarted == null && exited) {
        FX.eventbus.fire(ConnectEvent.stop())
        null
      } else
        this
    }
  }

  private fun <T: Any> connectIfPossible(input: VariableInput<T>, marker: Out.ColumnValues<out Any>) {
    if (input.variable.type == marker.columnId.type) {
      println("type matches: ${input.variable.type}")

      FX.eventbus.fire(ModelEvent.EventData.Connect(
          input.id,
          input.variable,
          ColumnConnection.ColumnValues(marker.columnId as ColumnId<T>)
      ).asEvent())
    }
  }


  class OnOutput(
      val output: Out.ColumnValues<out Any>
  ) : MouseEventHandler {
    override fun onEvent(mouseEvent: MappedMouseEvent, marker: IsMarker?): MouseEventHandler? {
      println("touched input: $output")
      return null
    }
  }

  val inputResolver = MouseEventHandlerResolver.forType(::OnInput)
  val outputResolver = MouseEventHandlerResolver.forType(::OnOutput)

}