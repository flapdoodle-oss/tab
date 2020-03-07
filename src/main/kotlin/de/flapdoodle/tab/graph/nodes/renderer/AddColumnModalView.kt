package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.tab.extensions.fire
import de.flapdoodle.tab.graph.nodes.renderer.events.ModelEvent
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.math.BigDecimal
import kotlin.reflect.KClass

class AddColumnModalView : View() {
  private var nodeId: NodeId.TableId? = null

  override val root = borderpane {
    center {
      val type = SimpleObjectProperty<KClass<out Any>>()
      val name = SimpleStringProperty()
      form {
        fieldset {
          label("Name")
          textfield(name) {  }
        }
        fieldset {
          label("Type")
          choicebox(type, listOf(String::class, BigDecimal::class, Int::class))
        }
        fieldset {
          button {
            text = "Add"
            action {
              val id = nodeId
              require(id!=null) {"nodeId is null"}
              ModelEvent.addColumn(id, name.value!!, type.value!!).fire()
              close()
            }
          }
        }
      }
    }
  }

  companion object {
    // put instance creation here
    fun openModalWith(nodeId: NodeId.TableId) {
      val view = find(AddColumnModalView::class)
      view.nodeId = nodeId
      view.openModal(stageStyle = javafx.stage.StageStyle.UNDECORATED)
    }
  }
}