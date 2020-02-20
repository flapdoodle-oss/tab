package de.flapdoodle.tab.graph.nodes.values

import de.flapdoodle.tab.bindings.listBinding
import de.flapdoodle.tab.bindings.mapToList
import de.flapdoodle.tab.bindings.mapped
import de.flapdoodle.tab.bindings.syncFrom
import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.Table
import javafx.beans.value.ObservableObjectValue
import javafx.collections.ObservableList
import javafx.scene.control.TableColumn
import javafx.scene.layout.VBox
import tornadofx.*

class TableNode(
    private val table: ObservableObjectValue<Table>
) : () -> VBox {

  val rows = table.mapToList(Table::rows)
  val columnList = table.mapToList(Table::columnIds)

  private fun <T : Any> tableColumn(columnId: ColumnId<T>?): TableColumn<Table.Row, T> {
    return TableColumn(columnId!!.name)
  }

  override fun invoke(): VBox {
    return VBox().apply {
      val table = tableview(rows) {
        columns.syncFrom(columnList) { tableColumn(it) }
      }
    }
  }
}