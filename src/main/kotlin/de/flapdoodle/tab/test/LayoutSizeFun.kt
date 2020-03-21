package de.flapdoodle.tab.test

import de.flapdoodle.tab.graph.nodes.renderer.modals.AddNodeModalView
import javafx.scene.control.Control
import javafx.scene.paint.Color
import tornadofx.*

class LayoutSizeFun : View("My View") {
  override val root = borderpane {
    center {
      style {
        borderWidth += box(1.0.px)
        borderColor += box(Color.RED)
      }
      hbox {
        style {
          borderWidth += box(1.0.px)
          borderColor += box(Color.BLUE)
        }
        label("some text")
        button("foo")

        maxWidth=Control.USE_PREF_SIZE
      }
    }
  }

  companion object {
    // put instance creation here
    fun open() {
      val view = find(LayoutSizeFun::class)
      view.openWindow(stageStyle = javafx.stage.StageStyle.DECORATED)
    }
  }

}
