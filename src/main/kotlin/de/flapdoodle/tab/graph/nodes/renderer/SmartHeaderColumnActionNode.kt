package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.controls.tables.SmartColumn
import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.Data
import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.tab.extensions.fire
import de.flapdoodle.tab.extensions.property
import de.flapdoodle.tab.graph.nodes.renderer.events.ModelEvent
import tornadofx.*

class SmartHeaderColumnActionNode(
    val nodeId: NodeId.TableId,
    val tableColumn: SmartColumn<Data.Row, *>
) : Fragment() {
  override val root = group {
    val columnId = tableColumn.property(ColumnId::class)
        ?: throw IllegalArgumentException("columnId not set")
    vbox {
      button {
        text = "X"
        action {
          ModelEvent.deleteColumn(nodeId, columnId).fire()
        }
      }
      prefWidthProperty().bind(tableColumn.widthProperty())
    }
  }

  companion object {
    fun factoryFor(nodeId: NodeId.TableId): (SmartColumn<Data.Row, *>) -> Fragment {
      return {
        SmartHeaderColumnActionNode(nodeId,it)
      }
    }
  }
}