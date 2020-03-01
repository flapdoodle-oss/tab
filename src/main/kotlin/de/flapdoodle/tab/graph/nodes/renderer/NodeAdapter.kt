package de.flapdoodle.tab.graph.nodes.renderer

import javafx.scene.Parent
import javafx.scene.layout.BorderPane
import tornadofx.*

class NodeAdapter(
    private val content: Fragment,
    private val inputs: Fragment? = null,
    private val outputs: Fragment? = null,
    private val configuration: Fragment? = null
) : Fragment() {
  override val root = borderpane {
    if (configuration!=null) {
      top {
        this += configuration.root
      }
    }
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