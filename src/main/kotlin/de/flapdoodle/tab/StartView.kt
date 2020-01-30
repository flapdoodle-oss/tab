package de.flapdoodle.tab

import de.flapdoodle.tab.graph.ZoomablePane
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import tornadofx.*

class StartView : View("My View") {
  init {
    primaryStage.width = 800.0
    primaryStage.height = 640.0
  }

  override val root = borderpane {
    top = label {
      text="Tab"

      background = Background(BackgroundFill(Color.RED,null,null))
    }

    center<ZoomablePane>()
  }
}
