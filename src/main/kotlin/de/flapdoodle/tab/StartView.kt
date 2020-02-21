package de.flapdoodle.tab

import de.flapdoodle.tab.bindings.WritableObservableValue
import de.flapdoodle.tab.data.Column
import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.Model
import de.flapdoodle.tab.data.Table
import de.flapdoodle.tab.graph.SampleNode
import de.flapdoodle.tab.graph.ZoomablePane
import de.flapdoodle.tab.graph.nodes.DummyNode
import de.flapdoodle.tab.graph.nodes.AbstractGraphNode
import de.flapdoodle.tab.graph.nodes.ModelRenderer
import de.flapdoodle.tab.graph.nodes.values.NewValuesNode
import de.flapdoodle.tab.graph.nodes.values.ValuesNode
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Group
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.paint.Color
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

    if (false) {
      (1..5).forEach {
        val x = ThreadLocalRandom.current().nextDouble(200.0, 400.0)
        val y = ThreadLocalRandom.current().nextDouble(200.0, 400.0)

        val node = object : AbstractGraphNode<Group>({
          Group().apply {
            rectangle {
              style {
                fill = Color.BLUE
                width = 20.0
                height = 20.0
              }
            }
          }
        }) {}

        node.moveTo(x, y)
        node.title = "Node($it)"
        zoomablePane.content += node
      }

      (1..5).forEach {
        val x = ThreadLocalRandom.current().nextDouble(200.0, 400.0)
        val y = ThreadLocalRandom.current().nextDouble(200.0, 400.0)

        val node = DummyNode()
        node.moveTo(x, y)
        node.title = "Dummy($it)"
        zoomablePane.content += node
      }

      (1..3).forEach {
        val x = ThreadLocalRandom.current().nextDouble(0.0, 400.0)
        val y = ThreadLocalRandom.current().nextDouble(0.0, 400.0)

        val node = ValuesNode()
        node.moveTo(x, y)
        node.title = "Value($it)"
        zoomablePane.content += node
      }

      val tableProperty = SimpleObjectProperty(Table()
          .add(Column.named<String>("name")
              .set(3, "Klaus"))
          .add(Column.named<Int>("age")
              .set(7, 99)))

      (1..2).forEach {
        val x = ThreadLocalRandom.current().nextDouble(0.0, 400.0)
        val y = ThreadLocalRandom.current().nextDouble(0.0, 400.0)

        val node = NewValuesNode(WritableObservableValue(tableProperty))
        node.moveTo(x, y)
        node.title = "New Value($it)"
        zoomablePane.content += node
      }
//    zoomablePane.content.apply {
//      children += SampleNode().root
//    }
    }

    ModelRenderer(zoomablePane.content)
        .setModel(Model()
        .add(Table()
            .add(Column.named<String>("foo")
                .set(4,"Test"))
            .add(Column.named<Int>("bar")
                .set(9,1))
        ))

    zoomablePane.content += SampleNode()
  }
}
