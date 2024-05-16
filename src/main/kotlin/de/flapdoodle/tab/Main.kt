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
) : StackPane() {
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

  private val all = WeightGridPane()
    .apply {
      setColumnWeight(0, 1.0)
      setRowWeight(0,0.0)
      setRowWeight(1,0.0)
      setRowWeight(2,1.0)
  }

  init {
    bindCss("main")
    
    addEventFilter(KeyEvent.KEY_RELEASED) { event ->
      if (event.code == KeyCode.ESCAPE) {
        adapter.execute(Command.Abort())
      }
    }
    all.children.add(menu.apply {
      WeightGridPane.setPosition(this, 0, 0)
    })
    all.children.add(tools.apply {
      WeightGridPane.setPosition(this, 0, 1)
    })
    all.children.add(adapter.apply {
      WeightGridPane.setPosition(this, 0, 2)
    })

    children.add(all)
  }
}