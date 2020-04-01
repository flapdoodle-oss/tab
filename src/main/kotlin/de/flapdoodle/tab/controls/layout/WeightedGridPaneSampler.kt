package de.flapdoodle.tab.controls.layout

import javafx.scene.control.Button
import javafx.scene.control.Label
import tornadofx.*

class WeightedGridPaneSampler  : View("Weighted Grid Pane") {
  override val root = borderpane {
    center {
//      style {
//        borderWidth += box(1.0.px)
//        borderColor += box(Color.RED)
//      }
      this += WeightedGridPane().apply {
        add(Button("test"), 0, 0)
        add(Button("test-1"), 1, 0)
        add(Button("test-11"), 1, 1)
      }

    }
    bottom {
      gridpane {
        add(Button("test"),0,0)
        add(Button("test-1"),1,0)
        add(Button("test-11"),1,1)
      }
    }
  }

  companion object {
    // put instance creation here
    fun open() {
      val view = find(WeightedGridPaneSampler::class)
      view.openModal(stageStyle = javafx.stage.StageStyle.DECORATED)
    }
  }
}
