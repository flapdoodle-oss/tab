package de.flapdoodle.tab.graph.nodes.values

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty

class Dummy(private val name: String?, private val age: Int?) {
  val nameProperty = SimpleStringProperty(name)
  val ageProperty = SimpleIntegerProperty().apply {
    if (age != null) {
      set(age)
    }
  }
}