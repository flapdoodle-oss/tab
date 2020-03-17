package de.flapdoodle.tab.controls

import javafx.event.EventType
import javafx.scene.AccessibleRole
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Labeled
import javafx.scene.control.Skin
import javafx.scene.control.skin.LabelSkin
import javafx.scene.paint.Color
import tornadofx.*

class SCell(text: String) : Button(text) {

  init {
    minWidth = 20.0
    minHeight = 20.0

    style {
      backgroundColor = multi(Color.GRAY)
      borderColor += box(Color.RED)
      borderWidth += box(1.0.px)
    }

    requestFocus()

    focusedProperty().addListener { _, _, focused ->
      if (focused) {
        style {
          backgroundColor = multi(Color.BLUE)
        }
      } else {
        style {
          backgroundColor = multi(Color.WHITE)
        }
      }
    }

  }
}