package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.bindings.flatMapObservable
import de.flapdoodle.tab.bindings.map
import de.flapdoodle.tab.bindings.mapNullable
import de.flapdoodle.tab.bindings.mapToList
import de.flapdoodle.tab.bindings.syncFrom
import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.Data
import de.flapdoodle.tab.data.Model
import de.flapdoodle.tab.data.calculations.VariableMap
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.tab.extensions.centerInTop
import de.flapdoodle.tab.extensions.findAllInTree
import de.flapdoodle.tab.extensions.property
import de.flapdoodle.tab.fx.SingleThreadMutex
import de.flapdoodle.tab.graph.nodes.ColumnValueChangeListener
import de.flapdoodle.tab.graph.nodes.connections.OutNode
import javafx.beans.binding.Binding
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.Parent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import tornadofx.*
import java.util.concurrent.ThreadLocalRandom

class ModelRenderer(private val pane: Pane) {
  private val nodeLayer = Group()
  private val connectionLayer = Group()

  private val modelProperty: ObjectProperty<Model> = SimpleObjectProperty(Model())
  private val dataProperty: ObjectProperty<Data> = SimpleObjectProperty(Data())
  private val calculationMutex = SingleThreadMutex()

  private var tableNodes: Map<NodeId<*>, Fragment> = emptyMap()

  private val idAndNode = modelProperty.mapToList { model ->
    model.nodeIds().map { it to model.node(it) }
  }

  private val graphNodes = nodeLayer.children.map {
    val parent = it as Parent
    val id = parent.property(NodeId::class) ?: throw IllegalArgumentException("node id not set")

    it to parent.findAllInTree(OutNode::class).map {
      it.out to parent.centerInTop(it)
    }
  }

  private val outBindings = graphNodes.flatMapObservable { it ->
    it!!.second.map { it.second }
  }


  init {
//    modelProperty.addListener(ChangeListener { observable, oldValue, newValue ->
//      if (false) {
//        renderModel(newValue ?: throw IllegalArgumentException("model not set"))
//      }
//    })

    dataProperty.addListener(ChangeListener { _, _, newValue ->
      println("data changed: $newValue")
      calculationMutex.tryExecute {
        println("calculate...")
        dataProperty.set(calculate(modelProperty.get(), newValue))
      }
    })

    nodeLayer.children.syncFrom(idAndNode) { pair ->
      nodeFor(pair!!.first).root.apply {
        this.property(NodeId::class, pair.first)
      }
    }

    connectionLayer.children.syncFrom(outBindings) {
      val r = ThreadLocalRandom.current().nextDouble(8.0) + 8.0
      Foo(r, it!!).root
    }

    pane += nodeLayer
    pane += connectionLayer
  }

  fun change(change: (Model) -> Model) {
    modelProperty.set(change(modelProperty.get()))
  }

  fun changeData(change: (Data) -> Data) {
    dataProperty.set(change(dataProperty.get()))
  }

  private fun calculate(model: Model, data: Data): Data {
    var currentData = data
    val calcTables = model.nodes().filterIsInstance<ConnectableNode.Calculated>()
    calcTables.forEach {
      val connections = model.connections(it.id)?.variableMappings ?: emptyList()
      val variableMap = VariableMap.variableMap(currentData, connections)
      currentData = it.calculate(currentData, variableMap)
    }
    return currentData
  }

  class Foo(
      private val r: Double,
      private val c: Binding<Point2D>
  ) : Fragment() {
    override val root = group {
      circle {
        centerXProperty().bind(c.mapNullable { it!!.x })
        centerYProperty().bind(c.mapNullable { it!!.y })
        fill = Color(0.3, 0.3, 0.3, 0.3)
        radius = r
      }
    }
  }

  private fun nodeFor(nodeId: NodeId<out ConnectableNode>): NodeAdapterGraphNode {
    val changeListener = object : ColumnValueChangeListener {
      override fun <T : Any> change(id: ColumnId<out T>, row: Int, value: T?) {
        changeData { d -> d.change(id, row, value) }
      }
    }

    return NodeAdapterGraphNode.graphNodeFor(nodeId, modelProperty, dataProperty, changeListener)
  }
}