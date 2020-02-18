package de.flapdoodle.tab.graph.nodes

import javafx.scene.layout.BorderPane
import tornadofx.*

class DummyNode : AbstractGraphNode<BorderPane>({
  BorderPane().apply {
    center = vbox {
      label("one")
      label("two")
    }

    left = Connections.nodeFor(Input(String::class), Input(Double::class))
    right = Connections.nodeFor(Output(String::class))
  }
})