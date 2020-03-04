package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.bindings.mapNullable
import de.flapdoodle.tab.bindings.mapFrom
import de.flapdoodle.tab.data.Data
import de.flapdoodle.tab.data.Model
import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.tab.graph.nodes.AbstractGraphNode
import javafx.beans.property.ObjectProperty
import javafx.scene.Node
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
        modelProperty: ObjectProperty<Model>,
        dataProperty: ObjectProperty<Data>
    ): NodeAdapterGraphNode {
      require(modelProperty.get() != null) { "model is null" }
      val node = when (id) {
        is NodeId.TableId -> tableNode(id, modelProperty, dataProperty)
        is NodeId.CalculatedId -> calculated(id, modelProperty, dataProperty)
      }

//      val x = ThreadLocalRandom.current().nextDouble(0.0, 400.0)
//      val y = ThreadLocalRandom.current().nextDouble(0.0, 400.0)
      node.moveTo(xOffset, yOffset)
      xOffset = xOffset + node.root.layoutBounds.width + 20.0
      count ++
      if (count>3) {
        xOffset = 0.0
        yOffset = yOffset + node.root.layoutBounds.height
      }

      node.titleProperty.mapFrom(modelProperty) { m -> m?.node(id)?.name ?: "<undefined>" }
      return node
    }

    private fun tableNode(
        id: NodeId.TableId,
        modelProperty: ObjectProperty<Model>,
        dataProperty: ObjectProperty<Data>
    ): NodeAdapterGraphNode {
      val nodeProperty = modelProperty.mapNullable { m -> m!!.node(id) }
      return NodeAdapterGraphNode {
        NodeAdapter(
            content = ColumnsNode(
                node = nodeProperty,
                data = dataProperty,
                editable = true
            ),
            outputs = ColumnOutputsNode(
                node = nodeProperty
            )
        )
      }
    }

    private fun calculated(
        id: NodeId.CalculatedId,
        modelProperty: ObjectProperty<Model>,
        dataProperty: ObjectProperty<Data>
    ): NodeAdapterGraphNode {
      val nodeProperty = modelProperty.mapNullable { m ->
        println("XX NodeAdapterGraphNode: mapNullable: node for $id")
        m!!.node(id)
      }

      nodeProperty.onChange {
        println("XX NodeAdapterGraphNode(calculated): nodeProperty changed to $it")
      }

      return NodeAdapterGraphNode {
        NodeAdapter(
            content = ColumnsNode(
                node = nodeProperty,
                data = dataProperty
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