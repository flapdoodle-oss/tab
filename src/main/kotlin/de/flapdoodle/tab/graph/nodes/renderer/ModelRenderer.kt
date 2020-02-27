package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.bindings.Registration
import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.Data
import de.flapdoodle.tab.data.calculations.VariableMap
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.Model
import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.tab.extensions.findAllInTree
import de.flapdoodle.tab.fx.SingleThreadMutex
import de.flapdoodle.tab.graph.nodes.ColumnValueChangeListener
import de.flapdoodle.tab.graph.nodes.connections.OutNode
import javafx.beans.InvalidationListener
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.scene.layout.Pane
import tornadofx.*

class ModelRenderer(private val pane: Pane) {
  private val modelProperty: ObjectProperty<Model> = SimpleObjectProperty(Model())
  private val dataProperty: ObjectProperty<Data> = SimpleObjectProperty(Data())
  private val calculationMutex = SingleThreadMutex()
  private var tableNodes: Map<NodeId<*>, Pair<Registration, Fragment>> = emptyMap()

  init {
    modelProperty.addListener(ChangeListener { observable, oldValue, newValue ->
      renderModel(newValue ?: throw IllegalArgumentException("model not set"))
    })

    dataProperty.addListener(ChangeListener { _, _, newValue ->
      println("data changed: $newValue")
      calculationMutex.tryExecute {
        println("calculate...")
        dataProperty.set(calculate(modelProperty.get(), newValue))
      }
    })

    modelProperty.addListener(InvalidationListener {
      //throw IllegalArgumentException("not implemented")
    })
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

  private fun renderModel(model: Model) {
    println("model: $model")
    val changes = changes(model, tableNodes, ::nodeFor)

    tableNodes = tableNodes + changes.nodesToAdd

    changes.nodesToRemove.forEach {
      it.first.remove()
      pane.children.remove(it.second.root)
    }

    changes.nodesToAdd.forEach {
      pane += it.value.second.root
    }

    tableNodes.forEach { nodeId, (_, fragment) ->
      val outNodes = fragment.root.findAllInTree(OutNode::class)
      println("for $nodeId")
      outNodes.forEach {
        println(" -> ${it.centerX()}, ${it.centerY()}")
      }
    }
  }

  companion object {
    private fun changes(
        model: Model,
        currentTableNodes: Map<NodeId<*>, Pair<Registration, Fragment>>,
        nodeFactory: (NodeId<out ConnectableNode>) -> Pair<Registration, NodeAdapterGraphNode>
    ): Change {
      val tablesStillThere = model.nodeIds()
      val currentVisibleTables = currentTableNodes.keys

      val removed = currentVisibleTables - tablesStillThere
      val new = tablesStillThere - currentVisibleTables

      println("removed: $removed")
      println("new: $new")

      val nodesToRemove = currentTableNodes.filterKeys { removed.contains(it) }.values

      val nodesToAdd = new.map { tableId ->
        val registrationAndNode = nodeFactory(tableId)

        tableId to registrationAndNode
      }.toMap()

      return Change(nodesToRemove, nodesToAdd)
    }

    class Change(
        val nodesToRemove: Collection<Pair<Registration, Fragment>>,
        val nodesToAdd: Map<NodeId<out ConnectableNode>, Pair<Registration, NodeAdapterGraphNode>>
    )
  }

  private fun nodeFor(nodeId: NodeId<out ConnectableNode>): Pair<Registration, NodeAdapterGraphNode> {
    val changeListener = object : ColumnValueChangeListener {
      override fun <T : Any> change(id: ColumnId<out T>, row: Int, value: T?) {
        changeData { d -> d.change(id, row, value) }
      }
    }

    return NodeAdapterGraphNode.graphNodeFor(nodeId, modelProperty, dataProperty, changeListener)
  }
}