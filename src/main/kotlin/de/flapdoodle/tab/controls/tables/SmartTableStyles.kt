package de.flapdoodle.tab.controls.tables

import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class SmartTableStyles : Stylesheet() {

  companion object {
    val smartTable by cssclass("smart-table")
    val smartHeader by cssclass("smart-header")
    val smartFooter by cssclass("smart-footer")
    val smartHeaderColumn by cssclass("smart-header-column")

    val smartRows by cssclass("smart-rows")
    val smartRow by cssclass("smart-row")
    val smartCell by cssclass("smart-cell")

    val moreBlack3 = Color(0.0, 0.0, 0.0, 0.03)
    val moreBlack5 = Color(0.0, 0.0, 0.0, 0.05)
    val moreBlack8 = Color(0.0, 0.0, 0.0, 0.08)
    val moreBlack10 = Color(0.0, 0.0, 0.0, 0.10)
    val moreBlack15 = Color(0.0, 0.0, 0.0, 0.15)
    val moreBlack20 = Color(0.0, 0.0, 0.0, 0.20)
  }

  init {
    smartTable {
      borderWidth += box(1.px, 1.px, 0.px, 0.px)
      borderColor += box(Color(0.0, 0.0, 0.0, 0.1))

      backgroundColor += Color.WHITE

      contains(smartHeader) {
        backgroundColor += moreBlack15

        contains(splitPane) {
          backgroundColor += Color.TRANSPARENT
          padding = box(0.px)

          contains(splitPaneDivider) {
            backgroundColor += moreBlack15
            padding = box(0.px).copy(left = 1.px)
          }
        }

        contains(smartHeaderColumn) {
          padding = box(4.px)
          fontWeight = FontWeight.BOLD
//          borderColor += box(Color.BLACK)
//          borderWidth += box(1.0.px)
//          textAlignment = TextAlignment.CENTER
        }
      }

      contains(smartFooter) {
        backgroundColor += moreBlack15
      }

      contains(smartRows) {
        backgroundColor += Color.WHITE

        contains(scrollPane) {
          padding = box(0.px)
        }


        contains(smartRow) {
          borderWidth += box(0.px).copy(top = 1.px)
          borderColor += box(moreBlack5)

          and(even) {
            backgroundColor += moreBlack3
          }

          and(hover) {
            backgroundColor += moreBlack5

            and(even) {
              backgroundColor += moreBlack8
            }
          }
        }
      }
    }

    smartCell {
      borderWidth += box(0.px).copy(left = 1.px)
      borderColor += box(moreBlack5)
      padding = box(2.px)

      and(focused) {
        borderWidth += box(1.px)
        borderColor += box(Color.valueOf("#039ed3"))
      }
    }

    if (false) {
      println("-----------------------------")
      println(this.render())
      println("-----------------------------")
    }
  }
}