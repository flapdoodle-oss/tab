package de.flapdoodle.tab.app

import de.flapdoodle.kfx.controls.graphmodeleditor.model.*
import de.flapdoodle.kfx.controls.graphmodeleditor.types.VertexId
import de.flapdoodle.kfx.extensions.withAnchors
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.Position
import de.flapdoodle.tab.app.model.Tab2Model
import de.flapdoodle.tab.app.ui.Tab2ModelAdapter
import de.flapdoodle.tab.app.ui.commands.Command
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import java.util.concurrent.atomic.AtomicInteger

class Main2() : BorderPane() {
  private val model = SimpleObjectProperty(Tab2Model())
  private val selectedVertex = SimpleObjectProperty<VertexId<String>>()
  private val selectedEdge = SimpleObjectProperty<Edge<String>>()
  private val vertexCounter = AtomicInteger(0)
  private val slotCounter = AtomicInteger(0)

//  private val eventListener = ModelEventListener<String> { event ->
//    when (event) {
//      is ModelEvent.ConnectTo -> {
//        model.value = model.value.add(Edge(event.startVertex, event.startSlot, event.endVertex, event.endSlot))
//      }
//      else -> {
//
//      }
//    }
//    true
//  }
  private val adapter = Tab2ModelAdapter(model).also { editor ->
    WeightGridPane.setPosition(editor, 0, 0)
  }
//  private val editorAdapter = GraphEditorModelAdapter(model, eventListener, DummyVertexContentFactory).also { editor ->
//    editor.selectedVerticesProperty().subscribe { selection ->
//      if (selection.size == 1) {
//        selectedVertex.value = selection.first()
//      } else {
//        selectedVertex.value = null
//      }
//    }
//    editor.selectedEdgesProperty().subscribe { selection ->
//      if (selection.size == 1) {
//        selectedEdge.value = selection.first()
//      } else {
//        selectedEdge.value = null
//      }
//    }
//    WeightGridPane.setPosition(editor, 0, 0)
//  }

  init {
//    background = Background.fill(Color.DARKGRAY)
//    children.add(Button("Hi"))
    addEventFilter(KeyEvent.KEY_RELEASED) { event ->
      if (event.code == KeyCode.ESCAPE) {
        adapter.execute(Command.Abort())
      }
    }

    center = WeightGridPane()
      .withAnchors(all = 0.0)
      .also { gridPane ->
        gridPane.setColumnWeight(0, 1000.0)
        gridPane.setColumnWeight(1, 1.0)
        gridPane.setRowWeight(1, 1.0)

        gridPane.children.add(adapter)
        gridPane.children.add(Button("!!").also { button ->
          button.minWidth = 40.0
          button.maxWidth = 80.0
          button.maxHeight = 40.0
          WeightGridPane.setPosition(button, 1, 0)
        })
      }
    bottom = FlowPane().also { flowPane ->
      flowPane.children.addAll(
        Button("+C").also { button ->
          button.onAction = EventHandler {
            adapter.execute(Command.AskForPosition(onSuccess = { pos ->
              model.set(model.get().addNode(Node.Constants("Values#"+vertexCounter.incrementAndGet(), position = Position(pos.x, pos.y))))
            }))
          }
        }
//        Button("-").also { button ->
//          button.visibleProperty().bind(selectedVertex.map { it != null })
//          button.managedProperty().bind(button.visibleProperty())
//          button.onAction = EventHandler {
//            val vertexId = selectedVertex.value
//            model.set(model.get().remove(vertexId))
//          }
//        },
//        Button("?").also { button ->
//          button.visibleProperty().bind(selectedVertex.map { it != null })
//          button.managedProperty().bind(button.visibleProperty())
//          button.onAction = EventHandler {
//            val vertexId = selectedVertex.value
//            editorAdapter.execute(Command.FindVertex(vertexId) { println("found:)") })
//          }
//        },
//        Button("-->").also { button ->
//          button.visibleProperty().bind(selectedVertex.map { it != null })
//          button.managedProperty().bind(button.visibleProperty())
//          button.onAction = EventHandler {
//            val vertexId = selectedVertex.value
//            val vertex = model.get().vertex(vertexId)
//            model.set(model.get().replace(vertex, vertex.add(Slot("x#"+slotCounter.incrementAndGet(), Slot.Mode.IN, Position.LEFT))))
//          }
//        },
//        Button("<--").also { button ->
//          button.visibleProperty().bind(selectedVertex.map { it != null })
//          button.managedProperty().bind(button.visibleProperty())
//          button.onAction = EventHandler {
//            val vertexId = selectedVertex.value
//            val vertex = model.get().vertex(vertexId)
//            model.set(model.get().replace(vertex, vertex.add(Slot("y#"+slotCounter.incrementAndGet(), Slot.Mode.OUT, Position.RIGHT))))
//          }
//        },
//        Button("X").also { button ->
//          button.visibleProperty().bind(selectedEdge.map { it != null })
//          button.managedProperty().bind(button.visibleProperty())
//          button.onAction = EventHandler {
//            val egde = selectedEdge.value
//            model.set(model.get().remove(egde))
//          }
//        }
      )
    }
  }
}