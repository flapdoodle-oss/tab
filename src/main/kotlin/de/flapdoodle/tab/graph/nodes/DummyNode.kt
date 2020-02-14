package de.flapdoodle.tab.graph.nodes

import javafx.scene.Node
import tornadofx.*

class DummyNode : AbstractGraphNode(){
  override fun content() = vbox {
    label("one")
    label("two")
  }
}