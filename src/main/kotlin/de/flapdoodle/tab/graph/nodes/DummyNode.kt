package de.flapdoodle.tab.graph.nodes

import javafx.scene.Node
import tornadofx.*

class DummyNode : AbstractGraphNode(){
  override fun content() = borderpane {
    center = vbox {
      label("one")
      label("two")
    }

    left = Connections.nodeFor(Input(String::class), Input(Double::class))
    right = Connections.nodeFor(Output(String::class))
  }
}