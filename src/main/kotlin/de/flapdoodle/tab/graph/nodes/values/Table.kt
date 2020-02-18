package de.flapdoodle.tab.graph.nodes.values

import de.flapdoodle.tab.graph.nodes.NodeFactory
import javafx.event.EventTarget
import javafx.scene.Parent
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import tornadofx.*

class Table : () -> VBox {
  val list = listOf(
      Dummy("Klaus", 17),
      Dummy(null, 19),
      Dummy(null, null)
  ).toObservable()

  override fun invoke(): VBox {
    return VBox().apply {
      val table = tableview(list) {
        isEditable = true
        vgrow = Priority.ALWAYS
        column("name", Dummy::nameProperty).makeEditable()
        column("age", Dummy::ageProperty).makeEditable()
      }


      hbox {
        textfield("foo") {
          prefWidthProperty().bind(table.columns.get(0).widthProperty())
        }
      }
    }
  }
}