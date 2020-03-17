package de.flapdoodle.tab

import de.flapdoodle.tab.controls.SpreadSheet
import de.flapdoodle.tab.controls.SpreadSheetPlayground
import de.flapdoodle.tab.controls.layout.LayoutFun
import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.NamedColumn
import de.flapdoodle.tab.data.NodePositions
import de.flapdoodle.tab.data.TabModel
import de.flapdoodle.tab.data.calculations.CalculationMapping
import de.flapdoodle.tab.data.calculations.Calculations
import de.flapdoodle.tab.data.calculations.EvalExCalculationAdapter
import de.flapdoodle.tab.data.nodes.ColumnConnection
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.values.Input
import de.flapdoodle.tab.extensions.fire
import de.flapdoodle.tab.extensions.subscribeEvent
import de.flapdoodle.tab.graph.ZoomablePane
import de.flapdoodle.tab.graph.nodes.AbstractGraphNode
import de.flapdoodle.tab.graph.nodes.renderer.ModelRenderer
import de.flapdoodle.tab.graph.nodes.renderer.events.DataEvent
import de.flapdoodle.tab.graph.nodes.renderer.events.IOEvent
import de.flapdoodle.tab.graph.nodes.renderer.events.ModelEvent
import de.flapdoodle.tab.graph.nodes.renderer.events.UIEvent
import de.flapdoodle.tab.graph.nodes.renderer.modals.AddNodeModalView
import de.flapdoodle.tab.graph.nodes.values.ValuesNode
import de.flapdoodle.tab.lazy.ChangeableValue
import de.flapdoodle.tab.persist.TabModelIO
import javafx.scene.Group
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import javafx.util.Duration
import tornadofx.*
import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.concurrent.ThreadLocalRandom

class StartView : View("My View") {
  private val model = ChangeableValue(TabModel())
  private val zoomablePane: ZoomablePane = find()
  private val renderer = ModelRenderer(zoomablePane.content, model)
  private var nodePositions = NodePositions()

  override val root = borderpane {
    top {
//      label {
//        text = "Tab"
//        style {
//          textFill = Color.WHITE
//          backgroundColor = multi(Color.RED)
//        }
//      }

      menubar {
        menu("Files") {
          item("Open") {
            action {
              IOEvent.load().fire()
            }
          }

          item("Save") {
            action {
              IOEvent.save().fire()
            }
          }
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
        button("Add") {
          onLeftClick {
            AddNodeModalView.openModal()
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
    val barColumnId = ColumnId.create<BigDecimal>()
    val numberColumnId = ColumnId.create<BigDecimal>()

    renderer.apply {
      change { model ->
        val source = ConnectableNode.Table("source")
            .add(fooColumnId, "foo")
            .add(barColumnId, "bar")
            .add(numberColumnId, "numbers")

        val stringOpSample = ConnectableNode.Calculated("string op",
            calculations = listOf(CalculationMapping(
                calculation = Calculations.Calc_1(
                    a = Input.Variable(String::class, "name"),
                    formula = { s -> ">$s<" }
                ),
                column = NamedColumn("nameCol", ColumnId.create())
            )))

//        val numberOpSample = ConnectableNode.Calculated("add 10",
//            calculations = listOf(CalculationMapping(
//                calculation = Calculations.Calc_1(
//                    a = Variable(Int::class, "x"),
//                    formula = { s -> s?.let { it + 10 } }
//                ),
//                column = NamedColumn("offset", ColumnId.create())
//            ))
//        )
//
        val otherNumSample = ConnectableNode.Calculated("formula",
            calculations = listOf(CalculationMapping(
                calculation = EvalExCalculationAdapter("a*10"),
                column = NamedColumn("result", ColumnId.create())
            ))
        )

        model.add(source)
            .add(stringOpSample)
//            .add(numberOpSample)
            .add(otherNumSample)
            .connect(stringOpSample.id, Input.Variable(String::class, "name"), ColumnConnection.ColumnValues(fooColumnId))
//            .connect(numberOpSample.id, Variable(Int::class, "x"), ColumnConnection.ColumnValues(barColumnId))
            .connect(otherNumSample.id, Input.Variable(BigDecimal::class, "a"), ColumnConnection.ColumnValues(numberColumnId))
      }

      changeData { data ->
        data.change(fooColumnId, 4, "Klaus")
      }

      subscribeEvent<ModelEvent> { event ->
        println("got message: $event")

        when (event.data) {
          is ModelEvent.EventData.FormulaChanged<out ConnectableNode> -> {
            println("formula changed: ${event.data}")

            renderer.change { model ->
              event.data.applyTo(model)
            }
          }

          is ModelEvent.EventData.Connect<out Any> -> {
            println("connect: ${event.data}")

            renderer.change { model ->
              event.data.applyTo(model)
            }
          }

          else -> {
            renderer.change { model -> event.data.applyTo(model) }
          }
        }

//        renderer.change { model ->
//          model
//        }
      }

      subscribeEvent<DataEvent> { event ->
        println("change data: ${event.data}")

        renderer.changeData { data ->
          event.data.applyTo(data)
        }
      }

      subscribeEvent<IOEvent> { event ->
        when (event.action) {
          IOEvent.Action.Load -> {
            val fileChooser = fileChooser()
            fileChooser.title = "Open File"
            val file = fileChooser.showOpenDialog(currentStage)
            println("load $file")
            if (file!=null) {
              val content = Files.readAllBytes(file.toPath())
              //model.value(TabModel())
              val (newModel,newPositions) = TabModelIO.fromJson(String(content, Charsets.UTF_8))
              model.value(newModel)
              nodePositions = newPositions
              println("Loaded $nodePositions")
              nodePositions.forEach { nodeId, pos, size ->
                UIEvent.moveNode(nodeId,pos,size).fire()
              }
            }
          }

          IOEvent.Action.Save -> {
            val fileChooser = fileChooser()
            fileChooser.title = "Save File"
            fileChooser.initialFileName = "sample.tab"
            val file = fileChooser.showSaveDialog(currentStage)
            println("write to $file")
            if (file!=null) {
              val json = TabModelIO.asJson(model.value(), nodePositions)
              Files.write(file.toPath(),json.toByteArray(Charsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)
            }
          }
        }
      }

      subscribeEvent<UIEvent> { event ->
        if (event.eventData is UIEvent.EventData.NodeMoved) {
          nodePositions = nodePositions.set(event.eventData.id, event.eventData.position, event.eventData.size)
        }
      }
    }

//    zoomablePane.content += SampleNode()
//    zoomablePane.content += DragPlayground()
    zoomablePane.content += SpreadSheetPlayground()
    if (false) {
      zoomablePane.content += SpreadSheet().apply {
        move(time = Duration.seconds(2.0), destination = javafx.geometry.Point2D(-40.0, -40.0))
      }
    }
    zoomablePane.content += LayoutFun()
  }

  private fun fileChooser(): FileChooser {
    return FileChooser().apply {
      extensionFilters.addAll(
          FileChooser.ExtensionFilter("All Files", "*.*"),
          FileChooser.ExtensionFilter("Tab File", "*.tab")
      )
    }
  }
}
