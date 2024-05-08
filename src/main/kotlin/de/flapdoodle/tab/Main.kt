package de.flapdoodle.tab

import de.flapdoodle.kfx.extensions.withAnchors
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Position
import de.flapdoodle.tab.model.change.ModelChange
import de.flapdoodle.tab.model.data.Column
import de.flapdoodle.tab.ui.ModelSolverWrapper
import de.flapdoodle.tab.ui.Tab2ModelAdapter
import de.flapdoodle.tab.ui.commands.Command
import de.flapdoodle.tab.ui.views.dialogs.NewCalculationDialog
import de.flapdoodle.tab.ui.views.dialogs.NewTableDialog
import de.flapdoodle.tab.ui.views.dialogs.NewValuesDialog
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import java.util.concurrent.atomic.AtomicInteger

class Main(
  private val modelWrapper: ModelSolverWrapper
) : BorderPane() {

//  private val model = SimpleObjectProperty(Tab2Model())
  private val selectedVertex = SimpleObjectProperty<Id<out de.flapdoodle.tab.model.Node>>()
  private val selectedEdge = SimpleObjectProperty<Tab2ModelAdapter.Output2Input>()
  private val vertexCounter = AtomicInteger(0)
  private val slotCounter = AtomicInteger(0)

//  private fun changeModel(change: (Tab2Model) -> Tab2Model) {
//    val changed = change(model.value)
//    val solved = Solver.solve(changed)
//    model.value = solved
//  }
//
//  private fun validModelChange(change: (Tab2Model) -> Tab2Model): Boolean {
//    try {
//      change(model.value)
//      return true
//    } catch (ex: Exception) {
//      return  false
//    }
//  }
//
//  private val eventListener = ModelEventListener { event ->
//    when (event) {
//      is ModelEvent.ConnectTo -> {
//        changeModel { current ->
//          current.connect(event.start, event.startDataOrInput, event.end, event.endDataOrInput)
//        }
//      }
//      is ModelEvent.TryToConnectTo -> {
//        validModelChange { it.connect(event.start, event.startDataOrInput, event.end, event.endDataOrInput) }
//      }
//      else -> {
//
//      }
//    }
//    true
//  }

  private val adapter = Tab2ModelAdapter(modelWrapper.model(), modelWrapper.eventListener(), modelWrapper.changeListener()).also { editor ->
    editor.selectedNodesProperty().subscribe { selection ->
      if (selection.size == 1) {
        selectedVertex.value = selection.first()
      } else {
        selectedVertex.value = null
      }
    }
    editor.selectedEdgesProperty().subscribe { selection ->
      if (selection.size == 1) {
        selectedEdge.value = selection.first()
      } else {
        selectedEdge.value = null
      }
    }
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
        Button("+V").also { button ->
          button.onAction = EventHandler {
            val node = NewValuesDialog.open()
            if (node!=null) {
              adapter.execute(Command.AskForPosition(onSuccess = { pos ->
                modelWrapper.changeModel { it.addNode(node.copy(position = Position(pos.x, pos.y))) }
              }))
            }
          }
        },
        Button("+T").also { button ->
          button.onAction = EventHandler {
            val node = NewTableDialog.open()
            if (node!=null) {
              val changed = node // fakeDummy(node)
              adapter.execute(Command.AskForPosition(onSuccess = { pos ->
                modelWrapper.changeModel { it.addNode(changed.copy(position = Position(pos.x, pos.y))) }
              }))
            }
          }
        },
        Button("+C").also { button ->
          button.onAction = EventHandler {
            val node = NewCalculationDialog.open()
            if (node!=null) {
              adapter.execute(Command.AskForPosition(onSuccess = { pos ->
//              val node = Node.Calculated(
//                name = "Calculation#" + vertexCounter.incrementAndGet(),
//                indexType = Int::class,
//                position = Position(pos.x, pos.y))
//                .addAggregation(Calculation.Aggregation("c",EvalAdapter("a+b")))

                modelWrapper.changeModel { it.addNode(node.copy(position = Position(pos.x, pos.y))) }
              }))
            }
          }
        },
        Button("x").also { button ->
          button.visibleProperty().bind(selectedVertex.map { it != null })
          button.managedProperty().bind(button.visibleProperty())
          button.onAction = EventHandler {
            val vertexId = selectedVertex.value
            modelWrapper.changeModel { it.removeNode(vertexId) }
          }
        },
        Button("-").also { button ->
          button.visibleProperty().bind(selectedEdge.map { it != null })
          button.managedProperty().bind(button.visibleProperty())
          button.onAction = EventHandler {
            val edge = selectedEdge.value
            modelWrapper.changeModel { it.disconnect(edge.id, edge.input, edge.source) }
          }
        },
        Button("*").also { button ->
          button.onAction = EventHandler {
            val vertexId = selectedVertex.value
            modelWrapper.changeModel {
              val node = de.flapdoodle.tab.model.Node.Table("Table", Int::class)
              val nameColumn = Column("Name", node.indexType, String::class)
              val ageColumn = Column("Age", node.indexType, Int::class)

              val tableNode = node
                .apply(ModelChange.AddColumn(node.id, nameColumn))
                .apply(ModelChange.AddColumn(node.id, ageColumn))

              val calcNode = de.flapdoodle.tab.model.Node.Calculated("Calc", Int::class)
              it.addNode(tableNode.copy(position = Position(30.0, 30.0)))
                .apply(ModelChange.SetColumns(tableNode.id, 1, listOf(nameColumn.id to "Klaus", ageColumn.id to 22)))
                .apply(ModelChange.SetColumns(tableNode.id, 2, listOf(ageColumn.id to 24)))
                .apply(ModelChange.SetColumns(tableNode.id, 3, listOf(nameColumn.id to "Susi", ageColumn.id to 45)))
                .apply(ModelChange.SetColumns(tableNode.id, 10, listOf(nameColumn.id to "Peter")))
                .addNode(calcNode.copy(position = Position(200.0, 30.0)))
                .apply(ModelChange.AddTabular(calcNode.id,"Copy","x"))
            }
          }
        }
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

  private fun <K: Comparable<K>> fakeDummy(node: de.flapdoodle.tab.model.Node.Table<K>): de.flapdoodle.tab.model.Node.Table<K> {
    return node
      .apply(ModelChange.AddColumn(node.id, Column("Name", node.indexType, String::class)))
      .apply(ModelChange.AddColumn(node.id, Column("Age", node.indexType, Int::class)))
  }
}