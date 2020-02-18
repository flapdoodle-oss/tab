package de.flapdoodle.tab.graph.nodes.values

import de.flapdoodle.tab.graph.nodes.AbstractGraphNode
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.TableView
import tornadofx.*

class ValuesNode : AbstractGraphNode() {

  override fun content() = table()

  fun table(): TableView<Dummy> {
    val list = listOf(
        Dummy("Klaus", 17),
        Dummy(null, 19),
        Dummy(null, null)
    ).toObservable()

    return tableview(list) {
      isEditable = true
      column("name", Dummy::nameProperty).makeEditable()
      column("age", Dummy::ageProperty).makeEditable()
    }
  }

  class Dummy(private val name: String?, private val age: Int?) {
    val nameProperty = SimpleStringProperty(name)
    val ageProperty = SimpleIntegerProperty().apply {
      if (age != null) {
        set(age)
      }
    }
  }
}