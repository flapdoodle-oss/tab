package de.flapdoodle.tab

import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import tornadofx.*

class StartView : View("My View") {
  override val root = borderpane {
    center = label {
      text="Tab"

      background = Background(BackgroundFill(Color.RED,null,null))
    }
  }
}
