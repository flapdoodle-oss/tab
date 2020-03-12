package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.data.Data
import de.flapdoodle.tab.data.TabModel
import de.flapdoodle.tab.data.nodes.ColumnConnection
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.tab.data.values.Input
import de.flapdoodle.tab.extensions.centerInTop
import de.flapdoodle.tab.extensions.findAllInTree
import de.flapdoodle.tab.extensions.property
import de.flapdoodle.tab.graph.nodes.connections.InNode
import de.flapdoodle.tab.graph.nodes.connections.Out
import de.flapdoodle.tab.graph.nodes.connections.OutNode
import de.flapdoodle.tab.lazy.ChangeableValue
import de.flapdoodle.tab.lazy.asAObservable
import de.flapdoodle.tab.lazy.bindFrom
import de.flapdoodle.tab.lazy.map
import de.flapdoodle.tab.lazy.merge
import de.flapdoodle.tab.lazy.syncFrom
import javafx.beans.binding.Binding
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.Pane
import tornadofx.*

class ModelRenderer(
    private val pane: Pane,
    private val modelProperty: ChangeableValue<TabModel>
) {
  private val nodeLayer = Group()
  private val connectionLayer = Group()

//  private val modelProperty = ChangeableValue(TabModel())
  private val nodesProperty = modelProperty.map { it.nodes }
  private val dataProperty = modelProperty.map { it.data }

  private val idsP = modelProperty.map { it.nodeIds().toList() }

  private val nodeLayerSyncReg2 = nodeLayer.children.bindFrom(idsP) { id ->
    println("node for $id")
    nodeForP(id!!).root.apply {
      this.property(NodeId::class, id!!)
    }
  }

  private val nodeConnectionsP = modelProperty.map { model ->
    println("XX ModelRenderer: modelProperty -> nodeConnections")
    println("XX ModelRenderer: $model")
    val ret = model.nodeIds().map { it to model.tableConnections(it) }.toMap()
    println("XX ModelRenderer: -> $ret")
    ret
  }

  private fun connectorPositions(list: List<Node?>): Map<NodeId<*>, ConnectorPositions> {
    println("XX ModelRenderer: connectorPositions - list: $list")
    val result = list.map {
      val parent = it as Parent
      val id = parent.property(NodeId::class) ?: throw IllegalArgumentException("node id not set")

      val out = parent.findAllInTree(OutNode::class).map {
        it.out to parent.centerInTop(it)
      }.toMap()

      val ins = parent.findAllInTree(InNode::class).map {
        it.input.variable to parent.centerInTop(it)
      }.toMap()

      id to ConnectorPositions(out, ins)
    }.toMap()
    return result
  }

  data class ConnectorPositions(
      val output: Map<Out<out Any>, Binding<Point2D>>,
      val input: Map<Input<out Any>, Binding<Point2D>>
  ) {
    operator fun get(connection: ColumnConnection<out Any>): Binding<Point2D> {
      return when (connection) {
        is ColumnConnection.ColumnValues<out Any> -> output[Out.ColumnValues(connection.columnId)]!!
        is ColumnConnection.Aggregate<out Any> -> output[Out.Aggregate(connection.columnId)]!!
      }
    }

    operator fun get(variable: Input<out Any>): Binding<Point2D> {
      val pos = input[variable]
      require(pos != null) { " could not find pos for $variable in $input" }
      return pos
    }
  }

  private val connectionNodesP = nodeConnectionsP.merge(nodeLayer.children.asAObservable()) { connections , children->
    val connectors = connectorPositions(children)

    println("connection nodes")
    println("connections: $connections")
    println("connectors: $connectors")

    connections.flatMap { entry ->
      println("connection: $entry")
      System.out.flush()

      if (entry.value.isNotEmpty()) {
        println("connection: not empty -> ${entry.value}")
        System.out.flush()

        val dstConnectors = connectors[entry.key]
        if (dstConnectors != null) {
          require(dstConnectors != null) { "connectors for ${entry.key} not found in $connectors" }
          entry.value.mapNotNull { c ->
            println("connection: entry value -> $c")
            val positions = connectors[c.sourceNode]
            if (positions!=null) {
//              require(positions != null) { "positions for ${c.sourceNode} not found in $connectors" }
              val start = positions[c.columnConnection]
              val end = dstConnectors[c.variable]
              Pair(start, end)
            } else null
          }
        } else emptyList()
      } else {
        emptyList()
      }
    }
  }

  private val connectionNodesRegP = connectionLayer.children.syncFrom(connectionNodesP) { pair ->
    ConnectionNode(pair.first, pair.second).root
  }

  init {
    pane += nodeLayer
    pane += connectionLayer
    pane += ShowConnectHandleNode()
  }

  fun change(change: (TabModel) -> TabModel) {
    modelProperty.value(change(modelProperty.value()))
  }


  fun changeData(change: (Data) -> Data) {
    modelProperty.value(modelProperty.value().applyDataChanges(change))
  }

  private fun nodeForP(nodeId: NodeId<out ConnectableNode>): NodeAdapterGraphNode {
    return NodeAdapterGraphNode.graphNodeFor(nodeId, nodesProperty, dataProperty)
  }
}