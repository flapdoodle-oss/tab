package de.flapdoodle.tab.graph.nodes.renderer.modals

import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.NamedColumn
import de.flapdoodle.tab.data.calculations.NumberAggregation
import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.fx.extensions.fire
import de.flapdoodle.tab.graph.nodes.renderer.events.ModelEvent
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class AddAggregationModalView : View() {
  private var nodeId: NodeId.AggregatedId? = null

  override val root = borderpane {
    center {
      val name = SimpleStringProperty()
      val type = SimpleObjectProperty(NumberAggregation.Type.Sum)

      form {
        fieldset {
          label("Name")
          textfield(name)
        }
        fieldset {
          label("Formula")
          choicebox(type, NumberAggregation.Type.values().asList())
        }
        fieldset {
          button {
            text = "Add"
            action {
              val currentNodeId = nodeId
              require(currentNodeId != null) { "nodeId not set" }
              ModelEvent.addAggregation(currentNodeId, NamedColumn(name.value, ColumnId.create()), NumberAggregation(type.value)).fire()
              close()
            }
          }
        }
      }
    }
  }

  companion object {
    fun openModalWith(nodeId: NodeId.AggregatedId) {
      val view = find(AddAggregationModalView::class)
      view.nodeId = nodeId
      view.openModal(stageStyle = javafx.stage.StageStyle.UNDECORATED)
    }
  }
}