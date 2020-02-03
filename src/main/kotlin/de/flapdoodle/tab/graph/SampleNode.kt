package de.flapdoodle.tab.graph

import javafx.scene.Parent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.BorderPane
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import tornadofx.*

class SampleNode : Fragment() {
  override val root = borderpane {
    prefWidth = 50.0
    prefHeight = 25.0

    style {
      backgroundColor = multi(Color.WHITE)
      backgroundRadius = multi(box(5.0.px))

      borderColor = multi(box(Color.BLACK, Color.RED))
      borderRadius += box(5.0.px, 10.0.px)
    }

    center = label {
      text = "Sample"
    }
  }
}