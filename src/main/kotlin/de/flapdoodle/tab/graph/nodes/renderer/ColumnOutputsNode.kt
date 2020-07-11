package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.HasColumns
import de.flapdoodle.tab.graph.nodes.connections.Out
import de.flapdoodle.tab.graph.nodes.connections.OutNode
import de.flapdoodle.fx.lazy.LazyValue
import de.flapdoodle.fx.lazy.map
import de.flapdoodle.fx.lazy.syncFrom
import tornadofx.*

class ColumnOutputsNode<T>(
    node: LazyValue<T>
) : Fragment()
    where T : HasColumns,
          T : ConnectableNode {
  private val columnList = node.map(HasColumns::columns)

  override val root = vbox {
    children.syncFrom(columnList) {
      OutNode(Out.ColumnValues(it!!.id))
    }
  }
}