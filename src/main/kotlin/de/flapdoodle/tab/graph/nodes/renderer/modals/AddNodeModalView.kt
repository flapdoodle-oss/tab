package de.flapdoodle.tab.graph.nodes.renderer.modals

import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.extensions.fire
import de.flapdoodle.tab.graph.nodes.renderer.events.ModelEvent
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.ButtonBar
import tornadofx.*

class AddNodeModalView : View() {
  enum class NodeType {
    Constants,
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
        buttonbar {
          button("Create", ButtonBar.ButtonData.OK_DONE) {
            enableWhen(model.valid)

            action {
              println("could do it")
              model.commit {
                val type = model.type.value
                require(type != null) { "how can we get here?" }
                println("create: $type")

                val node: ConnectableNode = when (type) {
                  NodeType.Constants -> ConnectableNode.Constants(
                      name = model.name.value
                  )
                  NodeType.Table -> ConnectableNode.Table(
                      name = model.name.value
                  )
                  NodeType.Calculation -> ConnectableNode.Calculated(
                      name = model.name.value
                  )
                  NodeType.Aggregation -> ConnectableNode.Aggregated(
                      name = model.name.value
                  )
                }

                ModelEvent.addNode(node).fire()
                close()
              }
            }
          }

          button("Abort", ButtonBar.ButtonData.CANCEL_CLOSE) {
            action {
              model.rollback(model.type, model.name)
              close()
            }
          }
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