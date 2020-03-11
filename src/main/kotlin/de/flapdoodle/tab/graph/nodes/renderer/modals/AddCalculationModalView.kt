package de.flapdoodle.tab.graph.nodes.renderer.modals

import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.NamedColumn
import de.flapdoodle.tab.data.calculations.EvalExCalculationAdapter
import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.tab.extensions.fire
import de.flapdoodle.tab.graph.nodes.renderer.events.ModelEvent
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class AddCalculationModalView : View() {
  private var nodeId: NodeId.CalculatedId? = null

  override val root = borderpane {
    center {
      val name = SimpleStringProperty()
      val formula = SimpleStringProperty()
      form {
        fieldset {
          label("Name")
          textfield(name) { }
        }
        fieldset {
          label("Formula")
          textfield(formula) { }
        }
        fieldset {
          button {
            text = "Add"
            action {
              val currentNodeId = nodeId
              require(currentNodeId != null) { "nodeId not set" }
              ModelEvent.addCalculation(currentNodeId, NamedColumn(name.value, ColumnId.create()), EvalExCalculationAdapter(formula.value)).fire()
              close()
            }
          }
        }
      }
    }
  }

  companion object {
    fun openModalWith(nodeId: NodeId.CalculatedId) {
      val view = find(AddCalculationModalView::class)
      view.nodeId = nodeId
      view.openModal(stageStyle = javafx.stage.StageStyle.UNDECORATED)
    }
  }
}