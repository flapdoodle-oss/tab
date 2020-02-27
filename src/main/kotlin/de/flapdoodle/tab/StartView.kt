package de.flapdoodle.tab

import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.NamedColumn
import de.flapdoodle.tab.data.calculations.CalculationMapping
import de.flapdoodle.tab.data.calculations.Calculations
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.values.Variable
import de.flapdoodle.tab.graph.SampleNode
import de.flapdoodle.tab.graph.ZoomablePane
import de.flapdoodle.tab.graph.nodes.DummyNode
import de.flapdoodle.tab.graph.nodes.AbstractGraphNode
import de.flapdoodle.tab.graph.nodes.renderer.ModelRenderer
import de.flapdoodle.tab.graph.nodes.values.ValuesNode
import javafx.scene.Group
import javafx.scene.paint.Color
import tornadofx.*
import java.util.concurrent.ThreadLocalRandom

class StartView : View("My View") {
  private val zoomablePane: ZoomablePane = find()
  private val renderer = ModelRenderer(zoomablePane.content)

  override val root = borderpane {
    top {
      label {
        text = "Tab"
        style {
          textFill = Color.WHITE
          backgroundColor = multi(Color.RED)
        }
      }
    }
//    top = label {
//      text = "Tab"
//
//      background = Background(BackgroundFill(Color.RED, null, null))
//    }

    center = zoomablePane.root

    bottom {
      hbox {
        button("+") {
          onLeftClick {
            renderer.change {
              it.add(ConnectableNode.Table("new instance")
                  .add(ColumnId.create<Int>(), "clicked"))
            }
          }
        }
      }
    }
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
    }

    val fooColumnId = ColumnId.create<String>()
    val barColumnId = ColumnId.create<Int>()

    renderer.apply {
      change { model ->
        val source = ConnectableNode.Table("source")
            .add(fooColumnId, "foo")
            .add(barColumnId, "bar")

        val stringOpSample = ConnectableNode.Calculated("string op",
            calculations = listOf(CalculationMapping(
                calculation = Calculations.Calc_1(
                    a = Variable(String::class, "name"),
                    formula = { s -> ">$s<" }
                ),
                column = NamedColumn("nameCol", ColumnId.create())
            )))

        val numberOpSample = ConnectableNode.Calculated("add 10",
            calculations = listOf(CalculationMapping(
                calculation = Calculations.Calc_1(
                    a = Variable(Int::class, "x"),
                    formula = { s -> s?.let { it + 10 } }
                ),
                column = NamedColumn("offset", ColumnId.create())
            ))
        )

        model.add(source)
            .add(stringOpSample)
            .add(numberOpSample)
            .connect(stringOpSample.id,Variable(String::class, "name"),fooColumnId)
            .connect(numberOpSample.id,Variable(Int::class, "x"), barColumnId)
//      connections = listOf(VariableMapping(
//          columnId = fooColumnId,
//          variable = Variable(String::class, "name")
//      )),
//      connections = listOf(VariableMapping(
//          columnId = barColumnId,
//          variable = Variable(Int::class, "x")
//      )),
      }

      changeData { data ->
        data.change(fooColumnId, 4, "Klaus")
      }
    }

    zoomablePane.content += SampleNode()
  }
}
