package de.flapdoodle.tab.graph.nodes.renderer.modals

import de.flapdoodle.tab.data.nodes.NodeId
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.Parent
import tornadofx.*

class AddNodeModalView : View() {
  enum class NodeType {
    Table,
    Calculation,
    Aggregation
  }

  private val nodeType = SimpleObjectProperty(NodeType.Table)
  private val nodeName = SimpleStringProperty()

  private val model = AddNodeViewModel(AddNode())

  override val root = borderpane {
    center {
      form {
        fieldset {
          label("Type")
          choicebox(model.type, NodeType.values().asList())
        }
        fieldset {
          label("Name")
          textfield(model.name).required()
        }
        button("Create") {
          enableWhen(model.valid)

          action {
            println("could do it")
          }
        }
      }
    }

    bottom {
      button("close") {
        action {
          close()
        }
      }
    }
  }

  class AddNode() {
    val typeProperty = SimpleObjectProperty(NodeType.Table)
    var type by typeProperty

    val nameProperty = SimpleStringProperty()
    var name by nameProperty
  }

  class AddNodeViewModel(addNode: AddNode) : ViewModel() {
    val name = bind { addNode.nameProperty }
    val type = bind { addNode.typeProperty }
  }

  companion object {
    // put instance creation here
    fun openModal() {
      val view = find(AddNodeModalView::class)
      view.openModal(stageStyle = javafx.stage.StageStyle.UNDECORATED)
    }
  }
}