package de.flapdoodle.tab.controls

import javafx.scene.layout.Region
import javafx.scene.paint.Color
import tornadofx.*

class SpreadSheet : Region() {
  private val grid = gridpane {
    style {
      backgroundColor = multi(Color.WHITE)
      borderColor += box(Color.RED)
      borderWidth += box(1.0.px)
    }
  }

  init {
    add(grid)

    (0..3).forEach {c ->
      (0..2).forEach { r ->
        grid.add(SpreadSheetCell("($c:$r)", r % 2 == 0),c,r)
      }
    }
  }
}