package de.flapdoodle.tab.controls.layout

import de.flapdoodle.tab.extensions.Key
import de.flapdoodle.tab.extensions.property
import javafx.collections.FXCollections
import javafx.scene.Node
import javafx.scene.control.Control
import javafx.scene.control.SkinBase

class WeightedGridPane() : Control() {

  companion object {
    private object Row : Key<Int>()
    private object Column: Key<Int>()
    private object Weight: Key<Int>()
  }

  private val skin = Skin(this)
  private val nodes = FXCollections.observableArrayList<Node>()

  override fun createDefaultSkin() = skin

  fun add(node: Node, row: Int, column: Int) {
    node.apply {
      property[Row] = row
      property[Column] = column
      property[Weight] = 1
    }
  }

  class Skin(
      control: WeightedGridPane
  ) : SkinBase<WeightedGridPane>(control) {

  }
}