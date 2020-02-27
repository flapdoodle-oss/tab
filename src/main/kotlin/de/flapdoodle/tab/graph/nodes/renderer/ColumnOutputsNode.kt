package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.bindings.mapToList
import de.flapdoodle.tab.bindings.syncFrom
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.HasColumns
import de.flapdoodle.tab.graph.nodes.connections.Out
import de.flapdoodle.tab.graph.nodes.connections.OutNode
import javafx.beans.value.ObservableValue
import tornadofx.*

class ColumnOutputsNode<T>(
    node: ObservableValue<T>
) : Fragment()
    where T : HasColumns,
          T : ConnectableNode {
  private val columnList = node.mapToList(HasColumns::columns)

  override val root = vbox {
    children.syncFrom(columnList) {
      OutNode(Out.ColumnValues(it!!.id))
    }
  }
}