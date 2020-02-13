package de.flapdoodle.tab

import de.flapdoodle.tab.graph.SampleNode
import de.flapdoodle.tab.graph.ZoomablePane
import de.flapdoodle.tab.graph.events3.AdvGraphNode
import de.flapdoodle.tab.graph.events3.GraphNode
import de.flapdoodle.tab.graph.nodes.AbstractGraphNode
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

//    zoomablePane.root += group {
//      rectangle {
//        style {
//          fill = Color.RED
//          width = 10.0
//          height = 20.0
//        }
//      }
//
//    }

//    (1..10).forEach {
//      val x = ThreadLocalRandom.current().nextInt(-100,100)
//      val y = ThreadLocalRandom.current().nextInt(-100,100)
//      zoomablePane.content.apply {
//        rectangle(x = x, y = y) {
//          style {
//            fill = Color.YELLOW
//            width = 10.0
//            height = 20.0
//          }
//        }
//      }
//    }
//
//    (1..5).forEach {
//      val x = ThreadLocalRandom.current().nextDouble(0.0,200.0)
//      val y = ThreadLocalRandom.current().nextDouble(0.0,200.0)
//
//      zoomablePane.content += AdvGraphNode(x,y)
//    }

    (1..5).forEach {
      val x = ThreadLocalRandom.current().nextDouble(200.0,400.0)
      val y = ThreadLocalRandom.current().nextDouble(200.0,400.0)

      val node = object : AbstractGraphNode(x, y) {

      }
      node.title = "Node($it)"
      zoomablePane.content += node
    }

//    zoomablePane.content.apply {
//      children += SampleNode().root
//    }

    zoomablePane.content += SampleNode()
  }
}
