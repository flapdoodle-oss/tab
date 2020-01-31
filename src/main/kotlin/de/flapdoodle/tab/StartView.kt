package de.flapdoodle.tab

import de.flapdoodle.tab.graph.ZoomablePane
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import tornadofx.*
import java.util.concurrent.ThreadLocalRandom

class StartView : View("My View") {
  private val zoomablePane: ZoomablePane = find()

  override val root = borderpane {
    top = label {
      text = "Tab"

      background = Background(BackgroundFill(Color.RED, null, null))
    }

    center = zoomablePane.root
  }

  init {
    primaryStage.width = 800.0
    primaryStage.height = 640.0

    zoomablePane.root += group {
      rectangle {
        style {
          fill = Color.RED
          width = 10.0
          height = 20.0
        }
      }

    }

    (1..10).forEach {
      val x = ThreadLocalRandom.current().nextInt(0,100)
      val y = ThreadLocalRandom.current().nextInt(0,100)
      zoomablePane.root.apply {
        rectangle(x = x, y = y) {
          style {
            fill = Color.YELLOW
            width = 10.0
            height = 20.0
          }
        }
      }
    }
  }
}
