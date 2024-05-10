package de.flapdoodle.tab

import de.flapdoodle.kfx.extensions.withAnchors
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.reflection.TypeInfo
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
  private val selectedVertex = SimpleObjectProperty<Id<out de.flapdoodle.tab.model.Node>>()
  private val selectedEdge = SimpleObjectProperty<Tab2ModelAdapter.Output2Input>()

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
  init {
    addEventFilter(KeyEvent.KEY_RELEASED) { event ->
      if (event.code == KeyCode.ESCAPE) {
        adapter.execute(Command.Abort())
      }
    }

    top = Toolbar(
      adapter = adapter::execute,
      modelChangeAdapter = modelWrapper::changeModel
    )
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
              val node = de.flapdoodle.tab.model.Node.Table("Table", TypeInfo.of(Int::class.javaObjectType))
              val nameColumn = Column("Name", node.indexType, TypeInfo.of(String::class.javaObjectType))
              val ageColumn = Column("Age", node.indexType, TypeInfo.of(Int::class.javaObjectType))

              val tableNode = node
                .apply(ModelChange.AddColumn(node.id, nameColumn))
                .apply(ModelChange.AddColumn(node.id, ageColumn))

              val calcNode = de.flapdoodle.tab.model.Node.Calculated("Calc", TypeInfo.of(Int::class.javaObjectType))
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
      )
    }
  }
}