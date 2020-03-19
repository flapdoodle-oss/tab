package de.flapdoodle.tab.controls.tables

import javafx.scene.Node
import javafx.scene.layout.StackPane
import tornadofx.*

abstract class SmartColumn<T : Any, C: Any>(
    header: Node,
    val footer: Node? = null
) : StackPane() {
  init {
    children.add(header)
//      maxWidth = 200.0
    addClass(SmartTableStyles.smartHeaderColumn)
  }

  abstract fun cell(row: T): SmartCell<T, C>
}