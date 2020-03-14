package de.flapdoodle.tab.graph.nodes.renderer

import javafx.scene.Parent
import javafx.scene.layout.BorderPane
import tornadofx.*

class NodeAdapter(
    private val content: Fragment,
    private val inputs: Fragment? = null,
    private val outputs: Fragment? = null,
    private val configuration: Fragment? = null,
    private val additional: Fragment? = null
) : Fragment() {
  override val root = borderpane {
    top {
      vbox {
        if (configuration != null) {
          this += configuration
        }
        if (additional!=null) {
          this += additional
        }
      }
    }
    val contentFragment = content

    center {
      this += content
//      tabpane {
//        tab("Data") {
//          isClosable = false
//
//          this += contentFragment
////          content += contentFragment
//        }
//      }
    }
    if (inputs != null) {
      left {
        this += inputs
      }
    }
    if (outputs != null) {
      right {
        this += outputs
      }
    }
  }
}