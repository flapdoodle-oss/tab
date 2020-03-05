package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.Data
import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.tab.extensions.fire
import de.flapdoodle.tab.extensions.property
import de.flapdoodle.tab.graph.nodes.renderer.events.ModelEvent
import javafx.scene.control.TableColumn
import tornadofx.*

class TableColumnActionNode(
    val nodeId: NodeId.TableId,
    val tableColumn: TableColumn<Data.Row, *>
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
    fun factoryFor(nodeId: NodeId.TableId): (TableColumn<Data.Row, *>) -> Fragment {
      return {
        TableColumnActionNode(nodeId,it)
      }
    }
  }
}