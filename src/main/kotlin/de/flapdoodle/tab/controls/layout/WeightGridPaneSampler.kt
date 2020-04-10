package de.flapdoodle.tab.controls.layout

import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.layout.ColumnConstraints
import javafx.scene.paint.Color
import tornadofx.*

class WeightGridPaneSampler : View("Weighted Grid Pane") {
  override val root = borderpane {
    center {
//      style {
//        borderWidth += box(1.0.px)
//        borderColor += box(Color.RED)
//      }

      this += WeightGridPane().apply {
        button("test") {
          minWidth = 20.0
          maxWidth = 100.0
          WeightGridPane.setPosition(this,0,0)
        }
        button("test-1") {
          WeightGridPane.setPosition(this,1,0)
        }
        button("test-11") {
          WeightGridPane.setPosition(this,1,1)
          maxHeight = 100.0
        }

        setColumnWeight(0, 1.0)
        setColumnWeight(1, 2.0)
        setRowWeight(0,4.0)
        setRowWeight(1,1.0)
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
      val view = find(WeightGridPaneSampler::class)
      view.openModal(stageStyle = javafx.stage.StageStyle.DECORATED)
    }
  }
}
