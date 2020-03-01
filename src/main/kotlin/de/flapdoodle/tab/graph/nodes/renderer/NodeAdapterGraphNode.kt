package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.bindings.mapNullable
import de.flapdoodle.tab.bindings.mapFrom
import de.flapdoodle.tab.data.Data
import de.flapdoodle.tab.data.Model
import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.tab.graph.nodes.AbstractGraphNode
import de.flapdoodle.tab.graph.nodes.ColumnValueChangeListener
import javafx.beans.property.ObjectProperty
import javafx.scene.Node
import tornadofx.*
import java.util.concurrent.ThreadLocalRandom

class NodeAdapterGraphNode(
    factory: () -> Fragment
) : AbstractGraphNode<Node>({ factory().root }) {

  companion object {
    fun graphNodeFor(
        id: NodeId<*>,
        modelProperty: ObjectProperty<Model>,
        dataProperty: ObjectProperty<Data>,
        changeListener: ColumnValueChangeListener
    ): NodeAdapterGraphNode {
      require(modelProperty.get() != null) { "model is null" }
      val node = when (id) {
        is NodeId.TableId -> tableNode(id, modelProperty, dataProperty, changeListener)
        is NodeId.CalculatedId -> calculated(id, modelProperty, dataProperty)
      }

      val x = ThreadLocalRandom.current().nextDouble(0.0, 400.0)
      val y = ThreadLocalRandom.current().nextDouble(0.0, 400.0)
      node.moveTo(x, y)

      node.titleProperty.mapFrom(modelProperty) { m -> m?.node(id)?.name ?: "<undefined>" }
      return node
    }

    private fun tableNode(
        id: NodeId.TableId,
        modelProperty: ObjectProperty<Model>,
        dataProperty: ObjectProperty<Data>,
        changeListener: ColumnValueChangeListener
    ): NodeAdapterGraphNode {
      val nodeProperty = modelProperty.mapNullable { m -> m!!.node(id) }
      return NodeAdapterGraphNode {
        NodeAdapter(
            content = ColumnsNode(
                node = nodeProperty,
                data = dataProperty,
                changeListener = changeListener
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
      val nodeProperty = modelProperty.mapNullable { m -> m!!.node(id) }
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