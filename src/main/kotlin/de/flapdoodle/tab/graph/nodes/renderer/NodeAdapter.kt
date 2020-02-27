package de.flapdoodle.tab.graph.nodes.renderer

import javafx.scene.Parent
import javafx.scene.layout.BorderPane
import tornadofx.*

class NodeAdapter(
    private val content: Fragment,
    private val inputs: Fragment? = null,
    private val outputs: Fragment? = null
) : Fragment() {
  override val root = borderpane {
    center {
      this += content.root
    }
    if (inputs != null) {
      left {
        this += inputs.root
      }
    }
    if (outputs != null) {
      right {
        this += outputs.root
      }
    }
  }
}