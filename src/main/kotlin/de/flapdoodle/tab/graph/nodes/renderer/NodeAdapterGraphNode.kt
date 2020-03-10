package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.data.Data
import de.flapdoodle.tab.data.Nodes
import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.tab.extensions.fire
import de.flapdoodle.tab.graph.nodes.AbstractGraphNode
import de.flapdoodle.tab.graph.nodes.renderer.events.ModelEvent
import de.flapdoodle.tab.lazy.LazyValue
import de.flapdoodle.tab.lazy.asBinding
import de.flapdoodle.tab.lazy.map
import de.flapdoodle.tab.lazy.mapNonNull
import javafx.scene.Node
import tornadofx.*
import javax.swing.UIDefaults

class NodeAdapterGraphNode(
    factory: () -> Fragment
) : AbstractGraphNode<Node>({ factory().root }) {

  companion object {
    private var xOffset = 0.0
    private var yOffset = 0.0
    private var count = 0

    fun graphNodeFor(
        id: NodeId<*>,
        nodesProperty: LazyValue<Nodes>,
        dataProperty: LazyValue<Data>
    ): NodeAdapterGraphNode {
      require(nodesProperty.value() != null) { "model is null" }
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

      node.titleProperty.bind(nodesProperty.map { m -> m?.find(id)?.name ?: "<undefined>" }.asBinding())
      return node
    }

    private fun tableNode(
        id: NodeId.TableId,
        nodesProperty: LazyValue<Nodes>,
        dataProperty: LazyValue<Data>
    ): NodeAdapterGraphNode {
      val nodeProperty = nodesProperty.mapNonNull { m ->
        println("XX NodeAdapterGraphNode: node for $id")
        m?.find(id)
      }

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
        nodesProperty: LazyValue<Nodes>,
        dataProperty: LazyValue<Data>
    ): NodeAdapterGraphNode {
      val nodeProperty = nodesProperty.mapNonNull { m ->
        println("XX NodeAdapterGraphNode: node for $id")
        m?.find(id)
      }

//      nodeProperty.onChange {
//        println("XX NodeAdapterGraphNode(calculated): nodeProperty changed to $it")
//      }

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