package de.flapdoodle.tab.controls.tables

import javafx.scene.paint.Color
import tornadofx.*

class SmartTableStyles : Stylesheet() {

  companion object {
    val smartTable by cssclass("smart-table")
    val smartHeader by cssclass("smart-header")
  }

  init {
    smartTable {
      borderWidth += box(1.px, 1.px, 0.px, 0.px)
      borderColor += box(Color(0.0, 0.0, 0.0, 0.1))

      backgroundColor += Color.WHITE

      child(scrollPane) {
        backgroundColor += Color.TRANSPARENT
      }

      child(smartHeader) {
        child(splitPane) {
          backgroundColor += Color.RED
        }
      }

      child(smartHeader, splitPane) {
        backgroundColor += Color.BLUE
      }
    }

    smartHeader {
      child(splitPane) {
//        backgroundColor += Color.GREEN
      }
    }

    println("-----------------------------")
    println(this.render())
    println("-----------------------------")
  }
}