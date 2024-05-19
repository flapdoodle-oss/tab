package de.flapdoodle.tab

import de.flapdoodle.kfx.extensions.bindCss
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
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane

class Main(
  private val modelWrapper: ModelSolverWrapper
) : WeightGridPane() {
  private val selectedVertex = SimpleObjectProperty<Id<out de.flapdoodle.tab.model.Node>>()
  private val selectedEdge = SimpleObjectProperty<Tab2ModelAdapter.Output2Input>()

  private val menu = AppMenu(modelWrapper)
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
  }

  private val tools = Tools(
    adapter = adapter::execute,
    modelChangeAdapter = modelWrapper::changeModel,
    selectedVertex = selectedVertex,
    selectedEdge = selectedEdge
  )

  init {
    bindCss("main")

    addEventFilter(KeyEvent.KEY_RELEASED) { event ->
      if (event.code == KeyCode.ESCAPE) {
        adapter.execute(Command.Abort())
      }
    }
    
    columnWeights(1.0)
    rowWeights(0.0, 0.0, 1.0)

    add(menu, 0, 0)
    add(tools, 0, 1)
    add(adapter, 0, 2)
  }
}