package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.HasInputs
import de.flapdoodle.tab.graph.nodes.connections.InNode
import de.flapdoodle.tab.graph.nodes.connections.VariableInput
import de.flapdoodle.tab.lazy.ChangedListener
import de.flapdoodle.tab.lazy.LazyValue
import de.flapdoodle.tab.lazy.map
import de.flapdoodle.tab.lazy.syncFrom
import tornadofx.*

class VariableInputsNode<T>(
    private val node: LazyValue<T>
) : Fragment()
    where T : HasInputs,
          T : ConnectableNode {
  private val inputs = node.map { node ->
    println("XX VariableInputsNode: node changed to $node")
    node.variables().toList().map { node.id to it }
  }

  init {
    node.addListener(ChangedListener { _ ->
      println("XX VariableInputsNode: node changed to(2) ${node.value()}")
    })
//    node.onChange {
//      println("XX VariableInputsNode: node changed to $it")
////      println("XX VariableInputsNode: should propagate to $inputs")
//    }
//    inputs.onChange {
//      println("XX VariableInputsNode: inputs changed to $it")
//    }
  }

  override val root = vbox {
    children.syncFrom(inputs) { it ->
      println("XX VariableInputsNode: syncFrom $it")
      val (id, v) = it!!
      InNode(VariableInput(id, v))
    }
  }
}