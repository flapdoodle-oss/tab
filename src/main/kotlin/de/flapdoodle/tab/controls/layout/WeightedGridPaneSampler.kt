package de.flapdoodle.tab.controls.layout

import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import tornadofx.*

class WeightedGridPaneSampler : View("Weighted Grid Pane") {
  override val root = borderpane {
    center {
//      style {
//        borderWidth += box(1.0.px)
//        borderColor += box(Color.RED)
//      }
      this += WeightedGridPane().apply {
        add(Button("test").apply {
          minWidth = 20.0; maxWidth = 100.0
        }, 0, 0)
        add(Button("test-1"), 1, 0)
        add(Button("test-11"), 1, 1)

        setColumnWeight(0, 1.0)
        setColumnWeight(1, 2.0)
      }

    }
    bottom {
      gridpane {
        add(Button("test"), 0, 0)
        add(Button("test-1"), 1, 0)
        add(Button("X").apply {
          maxWidth = Double.MAX_VALUE
          maxHeight = Double.MAX_VALUE
          style {
            fill = Color.RED
          }
        }, 1, 1)

        columnConstraints.add(ColumnConstraints().apply {
          percentWidth = 500.0
        })
        columnConstraints.add(ColumnConstraints().apply {
          percentWidth = 250.0
        })
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
