package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.bindings.mapNonNull
import de.flapdoodle.tab.bindings.mapTo
import de.flapdoodle.tab.bindings.mapToList
import de.flapdoodle.tab.bindings.mergeWith
import de.flapdoodle.tab.bindings.syncFrom
import de.flapdoodle.tab.data.Data
import de.flapdoodle.tab.data.Nodes
import de.flapdoodle.tab.data.calculation.Calculation
import de.flapdoodle.tab.data.nodes.ColumnConnection
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.tab.data.values.Variable
import de.flapdoodle.tab.extensions.centerInTop
import de.flapdoodle.tab.extensions.findAllInTree
import de.flapdoodle.tab.extensions.property
import de.flapdoodle.tab.fx.SingleThreadMutex
import de.flapdoodle.tab.graph.nodes.connections.InNode
import de.flapdoodle.tab.graph.nodes.connections.Out
import de.flapdoodle.tab.graph.nodes.connections.OutNode
import de.flapdoodle.tab.data.graph.ColumnGraph
import javafx.beans.binding.Binding
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.Pane
import tornadofx.*

class ModelRenderer(private val pane: Pane) {
  private val nodeLayer = Group()
  private val connectionLayer = Group()

  private val modelProperty: ObjectProperty<Nodes> = SimpleObjectProperty(Nodes())
  private val dataProperty: ObjectProperty<Data> = SimpleObjectProperty(Data())
  private val calculationMutex = SingleThreadMutex()

  private val ids = modelProperty.mapToList { model ->
    model.nodeIds().toList()
  }

  private val nodeLayerSyncReg = nodeLayer.children.syncFrom(ids) { id ->
    println("node for $id")
    nodeFor(id!!).root.apply {
      this.property(NodeId::class, id!!)
    }
  }

//  private val graphNodes = nodeLayer.childrenUnmodifiable.map {
//    val parent = it as Parent
//    val id = parent.property(NodeId::class) ?: throw IllegalArgumentException("node id not set")
//
//    id to parent.findAllInTree(OutNode::class).map {
//      it.out to parent.centerInTop(it)
//    }
//  }
//
//  private val outBindings = graphNodes.flatMapObservable { it ->
//    it!!.second.map { it.second }
//  }

  private val nodeConnections = modelProperty.mapNonNull { model ->
    println("XX ModelRenderer: modelProperty -> nodeConnections")
    println("XX ModelRenderer: $model")
    model.nodeIds().map { it to model.tableConnections(it) }.toMap()
  }

  // die initialisierungsreihenfolge ist schwer zu handhaben
  // vielleicht sollte man sowas einfach als event rumschicken
  private val nodeConnectors = nodeLayer.children.mapTo { it ->
    println("XX ModelRenderer: nodeLayer children ")
    it.forEach { println("XX ModelRenderer: child $it") }
    it
  }.mergeWith(modelProperty) { list, _ ->
    println("XX ModelRenderer: nodeConnectors ")
    println("list: $list")
    val result = connectorPositions(list)
    println("---> $result")
    result
  }

  private fun connectorPositions(list: List<Node?>): Map<NodeId<*>, ConnectorPositions> {
    val result = list.map {
      val parent = it as Parent
      val id = parent.property(NodeId::class) ?: throw IllegalArgumentException("node id not set")

      val out = parent.findAllInTree(OutNode::class).map {
        it.out to parent.centerInTop(it)
      }.toMap()

      val ins = parent.findAllInTree(InNode::class).map {
        it.variableInput.variable to parent.centerInTop(it)
      }.toMap()

      id to ConnectorPositions(out, ins)
    }.toMap()
    return result
  }

  data class ConnectorPositions(
      val output: Map<Out<out Any>, Binding<Point2D>>,
      val input: Map<Variable<out Any>, Binding<Point2D>>
  ) {
    operator fun get(connection: ColumnConnection<out Any>): Binding<Point2D> {
      return when (connection) {
        is ColumnConnection.ColumnValues<out Any> -> output[Out.ColumnValues(connection.columnId)]!!
        is ColumnConnection.Aggregate<out Any> -> output[Out.Aggregate(connection.columnId)]!!
      }
    }

    operator fun get(variable: Variable<out Any>): Binding<Point2D> {
      val pos = input[variable]
      require(pos!=null) {" could not find pos for $variable in $input"}
      return pos
    }
  }

  private val connectionNodes = nodeConnections.mapNonNull { connections->
    val connectors = connectorPositions(nodeLayer.children)

    println("connection nodes")
    println("connections: $connections")
    println("connectors: $connectors")

    connections.flatMap { entry ->
      println("connection: $entry")
      if (entry.value.isNotEmpty()) {
        val dstConnectors = connectors[entry.key]
        if (dstConnectors!=null) {
          require(dstConnectors != null) { "connectors for ${entry.key} not found in $connectors" }
          entry.value.map { c ->
            val start = (connectors[c.sourceNode]!!)[c.columnConnection]
            val end = dstConnectors[c.variable]
            Pair(start, end)
          }
        } else emptyList()
      } else {
        emptyList()
      }
    }
  }.mapToList {
    println("connections: $it")
    it
  }

  private val connectionNodesReg = connectionLayer.children.syncFrom(connectionNodes) { pair ->
    require(pair != null) { "pair is null" }
    ConnectionNode(pair.first, pair.second).root
  }


  init {
    modelProperty.onChange {
      println("XX ModelRenderer: model changed to $it")
    }

    dataProperty.onChange {
      println("XX ModelRenderer: data changed to $it")
    }

    ids.onChange {
      println("XX ModelRenderer: nodeIds changed to $it")
    }

    nodeConnections.onChange {
      println("XX ModelRenderer: nodeConnections changed to $it")
    }

    dataProperty.addListener(ChangeListener { _, _, newValue ->
      println("data changed: $newValue")
      calculationMutex.tryExecute {
        println("calculate...")
        dataProperty.set(Calculation.calculate(modelProperty.get(), newValue))
      }
    })

    modelProperty.addListener { observable, oldValue, newValue ->
      ColumnGraph.of(newValue)
    }

    modelProperty.addListener(tornadofx.ChangeListener { _, _, newModel ->
      calculationMutex.tryExecute {
        println("calculate...")
        dataProperty.set(Calculation.calculate(newModel, dataProperty.get()))
      }
    })

    pane += nodeLayer
    pane += connectionLayer
    pane += ShowConnectHandleNode()
  }

  fun change(change: (Nodes) -> Nodes) {
    val changed = change(modelProperty.get())
    if (changed==modelProperty.get()) {
      println("this change did nothing: $change")
    }
    modelProperty.set(changed)
  }

  fun changeData(change: (Data) -> Data) {
    dataProperty.set(change(dataProperty.get()))
  }

  private fun nodeFor(nodeId: NodeId<out ConnectableNode>): NodeAdapterGraphNode {
    return NodeAdapterGraphNode.graphNodeFor(nodeId, modelProperty, dataProperty)
  }
}