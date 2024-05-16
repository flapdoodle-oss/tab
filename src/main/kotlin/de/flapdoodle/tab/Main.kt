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
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane

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

    top = Tools(
      adapter = adapter::execute,
      modelChangeAdapter = modelWrapper::changeModel,
      selectedVertex = selectedVertex,
      selectedEdge
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
  }
}