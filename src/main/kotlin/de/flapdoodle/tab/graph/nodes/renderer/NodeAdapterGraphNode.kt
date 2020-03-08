package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.bindings.mapNullable
import de.flapdoodle.tab.bindings.mapFrom
import de.flapdoodle.tab.bindings.mapOnlyNonNull
import de.flapdoodle.tab.data.Data
import de.flapdoodle.tab.data.Nodes
import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.tab.extensions.fire
import de.flapdoodle.tab.graph.nodes.AbstractGraphNode
import de.flapdoodle.tab.graph.nodes.renderer.events.ModelEvent
import javafx.beans.binding.Binding
import javafx.beans.property.ObjectProperty
import javafx.scene.Node
import org.fxmisc.easybind.monadic.MonadicBinding
import tornadofx.*

class NodeAdapterGraphNode(
    factory: () -> Fragment
) : AbstractGraphNode<Node>({ factory().root }) {

  companion object {
    private var xOffset = 0.0
    private var yOffset = 0.0
    private var count = 0

    fun graphNodeFor(
        id: NodeId<*>,
        nodesProperty: MonadicBinding<Nodes>,
        dataProperty: MonadicBinding<Data>
    ): NodeAdapterGraphNode {
      require(nodesProperty.get() != null) { "model is null" }
      val node = when (id) {
        is NodeId.TableId -> tableNode(id, nodesProperty, dataProperty)
        is NodeId.CalculatedId -> calculated(id, nodesProperty, dataProperty)
      }

//      val x = ThreadLocalRandom.current().nextDouble(0.0, 400.0)
//      val y = ThreadLocalRandom.current().nextDouble(0.0, 400.0)
      node.moveTo(xOffset, yOffset)
      xOffset = xOffset + node.root.layoutBounds.width + 20.0
      count++
      if (count > 3) {
        xOffset = 0.0
        yOffset = yOffset + node.root.layoutBounds.height
      }

      node.titleProperty.mapFrom(nodesProperty) { m -> m?.find(id)?.name ?: "<undefined>" }
      return node
    }

    private fun tableNode(
        id: NodeId.TableId,
        nodesProperty: MonadicBinding<Nodes>,
        dataProperty: MonadicBinding<Data>
    ): NodeAdapterGraphNode {
      val nodeProperty = nodesProperty.mapNullable { m ->
        println("XX NodeAdapterGraphNode: mapNullable: node for $id")
        m?.find(id)
      }.mapOnlyNonNull()

      return NodeAdapterGraphNode {
        NodeAdapter(
            content = ColumnsNode(
                node = nodeProperty,
                data = dataProperty,
                columnHeader = TableColumnActionNode.factoryFor(id),
                columnFooter = ::TableColumnAggregateNode,
                editable = true,
                menu = {
                  item("Add Column").action {
                    AddColumnModalView.openModalWith(id)
                  }
                  item("Delete Table").action {
                    ModelEvent.deleteTable(id).fire()
                  }
                }
            ),
            outputs = ColumnOutputsNode(
                node = nodeProperty
            )
        )
      }
    }

    private fun calculated(
        id: NodeId.CalculatedId,
        nodesProperty: MonadicBinding<Nodes>,
        dataProperty: MonadicBinding<Data>
    ): NodeAdapterGraphNode {
      val nodeProperty = nodesProperty.mapNullable { m ->
        println("XX NodeAdapterGraphNode: mapNullable: node for $id")
        m?.find(id)
      }.mapOnlyNonNull()

      nodeProperty.onChange {
        println("XX NodeAdapterGraphNode(calculated): nodeProperty changed to $it")
      }

      return NodeAdapterGraphNode {
        NodeAdapter(
            content = ColumnsNode(
                node = nodeProperty,
                data = dataProperty,
                columnFooter = ::TableColumnAggregateNode,
                menu = {
                  item("Add Calculation").action {
                    AddCalculationModalView.openModalWith(id)
                  }
                  item("Delete Table").action {
                    ModelEvent.deleteTable(id).fire()
                  }
                }
            ),
            outputs = ColumnOutputsNode(
                node = nodeProperty
            ),
            inputs = VariableInputsNode(
                node = nodeProperty
            ),
            configuration = CalculationsNode(
                node = nodeProperty
            )
        )
      }
    }
  }
}