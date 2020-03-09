package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.HasColumns
import de.flapdoodle.tab.extensions.subscribeEvent
import de.flapdoodle.tab.graph.nodes.connections.Out
import de.flapdoodle.tab.graph.nodes.connections.OutNode
import de.flapdoodle.tab.graph.nodes.renderer.events.ConnectEvent
import de.flapdoodle.tab.observable.AObservable
import de.flapdoodle.tab.observable.map
import de.flapdoodle.tab.observable.syncFrom
import javafx.beans.value.ObservableValue
import tornadofx.*

class ColumnOutputsNode<T>(
    node: AObservable<T>
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