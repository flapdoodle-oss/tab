package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.HasInputs
import de.flapdoodle.tab.graph.nodes.connections.InNode
import de.flapdoodle.tab.graph.nodes.connections.VariableInput
import de.flapdoodle.tab.observable.AObservable
import de.flapdoodle.tab.observable.ChangeListener
import de.flapdoodle.tab.observable.map
import de.flapdoodle.tab.observable.syncFrom
import tornadofx.*

class VariableInputsNode<T>(
    private val node: AObservable<T>
) : Fragment()
    where T : HasInputs,
          T : ConnectableNode {
  private val inputs = node.map { node ->
    println("XX VariableInputsNode: node changed to $node")
    node.variables().toList().map { node.id to it }
  }

  init {
    node.addListener(ChangeListener { _,_,it ->
      println("XX VariableInputsNode: node changed to(2) $it")
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