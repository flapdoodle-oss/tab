package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.Data
import de.flapdoodle.tab.extensions.property
import de.flapdoodle.tab.graph.nodes.connections.Out
import de.flapdoodle.tab.graph.nodes.connections.OutNode
import javafx.scene.control.TableColumn
import tornadofx.*

class TableColumnAggregateNode(
    val tableColumn: TableColumn<Data.Row, *>
) : Fragment() {
  override val root = group {
    val columnId = tableColumn.property(ColumnId::class)
        ?: throw IllegalArgumentException("columnId not set")

    this += OutNode(Out.Aggregate(columnId)).apply {
      prefWidthProperty().bind(tableColumn.widthProperty())
    }
  }

}