package de.flapdoodle.tab.extensions

import javafx.beans.property.ObjectProperty

fun <T : Any> ObjectProperty<T>.change(change: (T) -> T) {
  val unchanged = value
  require(unchanged != null) { "value not set" }
  value = change(unchanged)
}