package de.flapdoodle.tab.graph.nodes.values

import javafx.scene.input.MouseButton
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import tornadofx.*

class DummyTableNode() : () -> VBox {
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
        column("name", Dummy::nameProperty) {
          isReorderable = false
          isSortable = false
          val cm = contextmenu {
            isAutoHide = false
            item("remove me") {

            }
          }
          setOnMouseClicked {
            if (it.button == MouseButton.SECONDARY) {
              cm.show(this@apply, it.screenX, it.screenY)
              it.consume()
            }
          }
        }.makeEditable()
        column("age", Dummy::ageProperty) {
          isReorderable = false
        }.makeEditable()
      }


      hbox {
        textfield("foo") {
          prefWidthProperty().bind(table.columns.get(0).widthProperty())
        }
      }
    }
  }
}