package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.bindings.Registration
import de.flapdoodle.tab.bindings.mapToList
import de.flapdoodle.tab.bindings.syncFrom
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.HasInputs
import de.flapdoodle.tab.extensions.property
import de.flapdoodle.tab.graph.nodes.connections.InNode
import de.flapdoodle.tab.graph.nodes.connections.VariableInput
import javafx.beans.value.ObservableValue
import tornadofx.*

class VariableInputsNode<T>(
    node: ObservableValue<T>
) : Fragment()
    where T : HasInputs,
          T : ConnectableNode {
  private val inputs = node.mapToList {node ->
    node.variables().toList().map { node.id to it }
  }

  init {
    node.onChange {
      println("XX VariableInputsNode: node changed to $it")
//      println("XX VariableInputsNode: should propagate to $inputs")
    }
    inputs.onChange {
      println("XX VariableInputsNode: inputs changed to $it")
    }
  }

  override val root = vbox {
    children.syncFrom(inputs) { it ->
      val (id, v) = it!!
      InNode(VariableInput(id, v))
    }
  }
}