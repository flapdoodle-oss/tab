package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.data.NamedColumn
import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.tab.extensions.fire
import de.flapdoodle.tab.graph.nodes.renderer.events.ModelEvent
import tornadofx.*

class ColumnActionNode(
    private val nodeId: NodeId.TableId,
    private val namedColumn: NamedColumn<out Any>
) : Fragment() {
  override val root = button {
    val columnId = namedColumn.id
      text = "X"
      action {
        ModelEvent.deleteColumn(nodeId, columnId).fire()
      }
    }

  companion object {
    fun factoryFor(nodeId: NodeId.TableId): (NamedColumn<out Any>) -> Fragment {
      return {
        ColumnActionNode(nodeId,it)
      }
    }
  }
}