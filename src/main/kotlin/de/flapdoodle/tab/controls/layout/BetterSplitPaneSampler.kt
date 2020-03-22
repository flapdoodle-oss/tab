package de.flapdoodle.tab.controls.layout

import de.flapdoodle.tab.test.LayoutSizeFun
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.paint.Color
import tornadofx.*

class BetterSplitPaneSampler : View("Better Split Pane") {
  override val root = borderpane {
    center {
      style {
        borderWidth += box(1.0.px)
        borderColor += box(Color.RED)
      }
      this += BetterSplitPane().apply {
        add(Label("one"))
        add(Label("two"))
      }
    }
    bottom {
      splitpane {
        label("one")
        label("two")
      }
    }
  }

  companion object {
    // put instance creation here
    fun open() {
      val view = find(BetterSplitPaneSampler::class)
      view.openWindow(stageStyle = javafx.stage.StageStyle.DECORATED)
    }
  }
}
