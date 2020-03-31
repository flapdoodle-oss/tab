package de.flapdoodle.tab.test

import javafx.scene.Parent
import javafx.scene.layout.Priority
import tornadofx.*

class GridLayoutFun : View("Grid Layout fun") {

  override val root = vbox {
    label("woohoo")

    gridpane {
      vgrow = Priority.ALWAYS
      hgrow = Priority.ALWAYS

      row {
        label("One")
        label("2")
        label("...")
      }
      row {
        label("---BIG---")
        label(" fly ")
        button("Click me") {
          //prefWidth = Prefe
          maxWidth = Double.MAX_VALUE
          gridpaneConstraints {
            fillWidth = true
            fillHeight = true
            hgrow = Priority.ALWAYS
            vgrow = Priority.ALWAYS
          }
        }
      }
    }
  }

  companion object {
    // put instance creation here
    fun open() {
      val view = find(GridLayoutFun::class)
      view.openModal(stageStyle = javafx.stage.StageStyle.DECORATED)
    }
  }

}