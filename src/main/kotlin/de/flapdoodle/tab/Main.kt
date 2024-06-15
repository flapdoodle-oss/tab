package de.flapdoodle.tab

import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.ui.ModelSolverWrapper
import de.flapdoodle.tab.ui.ModelAdapter
import de.flapdoodle.tab.ui.commands.Command
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent

class Main(
  private val modelWrapper: ModelSolverWrapper
) : WeightGridPane() {
  private val selectedVertex = SimpleObjectProperty<Id<out de.flapdoodle.tab.model.Node>>()
  private val selectedEdge = SimpleObjectProperty<ModelAdapter.Output2Input>()

  private val menu = AppMenu(modelWrapper)
  private val adapter = ModelAdapter(modelWrapper.model(), modelWrapper.eventListener(), modelWrapper.changeListener()).also { editor ->
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