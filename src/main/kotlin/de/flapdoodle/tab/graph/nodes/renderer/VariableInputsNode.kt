package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.bindings.mapToList
import de.flapdoodle.tab.bindings.syncFrom
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.HasInputs
import de.flapdoodle.tab.graph.nodes.connections.InNode
import de.flapdoodle.tab.graph.nodes.connections.VariableInput
import javafx.beans.value.ObservableValue
import tornadofx.*

class VariableInputsNode<T>(
    node: ObservableValue<T>
) : Fragment()
    where T : HasInputs,
          T : ConnectableNode {
  private val inputs = node.mapToList {
    println("mapToList: variables for $it")
    it.variables().toList()
  }

  override val root = vbox {
    children.syncFrom(inputs) {
      InNode(VariableInput(it!!))
    }
  }
}