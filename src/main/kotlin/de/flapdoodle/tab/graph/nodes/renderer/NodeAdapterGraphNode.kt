package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.data.Data
import de.flapdoodle.tab.data.Nodes
import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.tab.extensions.fire
import de.flapdoodle.tab.extensions.subscribeEvent
import de.flapdoodle.tab.graph.nodes.AbstractGraphNode
import de.flapdoodle.tab.graph.nodes.renderer.events.ModelEvent
import de.flapdoodle.tab.graph.nodes.renderer.events.UIEvent
import de.flapdoodle.tab.graph.nodes.renderer.modals.AddAggregationModalView
import de.flapdoodle.tab.graph.nodes.renderer.modals.AddCalculationModalView
import de.flapdoodle.tab.graph.nodes.renderer.modals.AddColumnModalView
import de.flapdoodle.fx.lazy.LazyValue
import de.flapdoodle.fx.lazy.asBinding
import de.flapdoodle.fx.lazy.map
import de.flapdoodle.fx.lazy.mapNonNull
import javafx.scene.Node
import tornadofx.*

class NodeAdapterGraphNode(
    factory: () -> Fragment
) : AbstractGraphNode<Node>({ factory().root }) {

  companion object {
    private var xOffset = 0.0
    private var yOffset = 0.0
    private var count = 0

    private fun debug(msg: String) {
      if (false) {
        println(msg)
      }
    }

    fun graphNodeFor(
        id: NodeId<*>,
        nodesProperty: LazyValue<Nodes>,
        dataProperty: LazyValue<Data>
    ): NodeAdapterGraphNode {
      require(nodesProperty.value() != null) { "model is null" }
      val node = when (id) {
        is NodeId.ConstantsId -> constantsNode(id, nodesProperty, dataProperty)
        is NodeId.TableId -> tableNode(id, nodesProperty, dataProperty)
        is NodeId.CalculatedId -> calculated(id, nodesProperty, dataProperty)
        is NodeId.AggregatedId -> aggregated(id, nodesProperty, dataProperty)
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
      node.addListener { pos,size ->
        UIEvent.nodeMoved(id,pos,size).fire()
      }
      node.apply {
        subscribeEvent<UIEvent> {event ->
          debug("XX NodeAdapterGraphNode -> got $event")
          if (event.eventData is UIEvent.EventData.MoveNode) {
            if (event.eventData.id == id) {
              debug("should move node $event")
              val eventData: UIEvent.EventData.MoveNode = event.eventData

              node.moveTo(eventData.position.x, eventData.position.y)
              node.resizeTo(eventData.size.width, eventData.size.height)
            }
          }
        }
      }

      return node
    }

    private fun constantsNode(
        id: NodeId.ConstantsId,
        nodesProperty: LazyValue<Nodes>,
        dataProperty: LazyValue<Data>
    ): NodeAdapterGraphNode {
      val nodeProperty = nodesProperty.mapNonNull { m ->
        debug("XX NodeAdapterGraphNode: node for $id")
        m?.find(id)
      }

      return NodeAdapterGraphNode {
        NodeAdapter(
            content = ConstantsNode(
                id = id,
                node = nodeProperty,
                data = dataProperty
                //columnHeader = ColumnActionNode.factoryFor(id),
                //columnFooter = ::SmartHeaderColumnAggregateNode,
//                editable = true,
//                menu = {
//                  item("Add Column").action {
//                    AddConstantModalView.openModalWith(id)
//                  }
//                  item("Delete Table").action {
//                    ModelEvent.deleteTable(id).fire()
//                  }
//                }
            ),
            outputs = ColumnOutputsNode(
                node = nodeProperty
            )
        )
      }
    }

    private fun tableNode(
        id: NodeId.TableId,
        nodesProperty: LazyValue<Nodes>,
        dataProperty: LazyValue<Data>
    ): NodeAdapterGraphNode {
      val nodeProperty = nodesProperty.mapNonNull { m ->
        debug("XX NodeAdapterGraphNode: node for $id")
        m?.find(id)
      }

      return NodeAdapterGraphNode {
        NodeAdapter(
            content = SmartColumnsNode(
                node = nodeProperty,
                data = dataProperty,
                columnHeader = ColumnActionNode.factoryFor(id),
                columnFooter = ::SmartHeaderColumnAggregateNode,
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
        debug("XX NodeAdapterGraphNode: node for $id")
        m?.find(id)
      }

//      nodeProperty.onChange {
//        debug("XX NodeAdapterGraphNode(calculated): nodeProperty changed to $it")
//      }

      return NodeAdapterGraphNode {
        NodeAdapter(
            content = SmartColumnsNode(
                node = nodeProperty,
                data = dataProperty,
//                columnFooter = ::TableColumnAggregateNode,
                columnFooter = ::SmartHeaderColumnAggregateNode,

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
                id = id,
                node = nodeProperty
            )
        )
      }
    }

    private fun aggregated(
        id: NodeId.AggregatedId,
        nodesProperty: LazyValue<Nodes>,
        dataProperty: LazyValue<Data>
    ): NodeAdapterGraphNode {
      val nodeProperty = nodesProperty.mapNonNull { m ->
        debug("XX NodeAdapterGraphNode: node for $id")
        m?.find(id)
      }

//      nodeProperty.onChange {
//        debug("XX NodeAdapterGraphNode(calculated): nodeProperty changed to $it")
//      }

      return NodeAdapterGraphNode {
        NodeAdapter(
            content = SmartColumnsNode(
                node = nodeProperty,
                data = dataProperty,
                menu = {
                  item("Add Aggregation").action {
                    AddAggregationModalView.openModalWith(id)
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
            )
//            configuration = CalculationsNode(
//                node = nodeProperty
//            )
        )
      }
    }
  }

}