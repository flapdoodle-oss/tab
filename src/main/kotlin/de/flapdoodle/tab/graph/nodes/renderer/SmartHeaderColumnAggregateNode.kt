package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.controls.tables.SmartColumn
import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.Data
import de.flapdoodle.tab.extensions.property
import de.flapdoodle.tab.graph.nodes.connections.Out
import de.flapdoodle.tab.graph.nodes.connections.OutNode
import tornadofx.*

class SmartHeaderColumnAggregateNode(
    val tableColumn: SmartColumn<Data.Row, *>
) : Fragment() {
  override val root = group {
    val columnId = tableColumn.property(ColumnId::class)
        ?: throw IllegalArgumentException("columnId not set")

    this += OutNode(Out.Aggregate(columnId)).apply {
      prefWidthProperty().bind(tableColumn.widthProperty())
    }
  }

}