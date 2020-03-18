package de.flapdoodle.tab.controls.tables

import de.flapdoodle.tab.converter.Converters
import javafx.beans.property.ReadOnlyDoubleProperty

interface SmartColumn<T: Any, C: Any> {
  val column: Column<T, C>

  fun widthProperty() : ReadOnlyDoubleProperty
  fun converter() = Converters.converterFor(column.type)
}