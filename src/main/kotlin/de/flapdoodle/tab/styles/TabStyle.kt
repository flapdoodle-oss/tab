package de.flapdoodle.tab.styles

import javafx.scene.paint.Color
import tornadofx.*

class TabStyle : Stylesheet() {

  companion object {
    //    val blackBorder by cssclass()
    val spreadSheedCell by cssclass("spreadsheet-cell")

    //    val odd by csspseudoclass("odd")
    val odd by csspseudoclass("odd")
    val even by csspseudoclass("even")
  }

  init {
//    blackBorder {
//      borderWidth += box(1.0.px)
//      borderColor += box(Color.BLACK)
//
//      and(hover) {
//        borderColor += box(Color.RED)
//      }
//    }

    val blackBorder = mixin {
      borderWidth += box(1.px,1.px,0.px,0.px)
      borderColor += box(Color(0.0,0.0,0.0,0.1))

      and(hover) {
        borderColor += box(Color.RED)
      }
    }

    spreadSheedCell {
//      +blackBorder
      borderWidth += box(1.px,1.px,0.px,0.px)
      borderColor += box(Color(0.0,0.0,0.0,0.1))

      backgroundColor += Color.WHITE
//      padding = box(10.0.px)

      and(hover) {
        backgroundColor += Color(0.0,0.0,0.0,0.05)
      }

      and(odd) {
        backgroundColor += Color(0.0,0.0,0.0,0.03)

        and(hover) {
          backgroundColor += Color(0.0,0.0,0.0,0.08)
        }
      }
    }

    if (false) {
      label {
        textFill = Color.RED

        fontSize = 56.px
        padding = box(5.px, 10.px)
        maxWidth = infinity
      }
    }
  }
}