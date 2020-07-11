package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.HasInputs
import de.flapdoodle.tab.data.values.Input
import de.flapdoodle.tab.graph.nodes.connections.In
import de.flapdoodle.tab.graph.nodes.connections.InNode
import de.flapdoodle.fx.lazy.ChangedListener
import de.flapdoodle.fx.lazy.LazyValue
import de.flapdoodle.fx.lazy.map
import de.flapdoodle.fx.lazy.syncFrom
import tornadofx.*

class VariableInputsNode<T>(
    private val node: LazyValue<T>
) : Fragment()
    where T : HasInputs,
          T : ConnectableNode {

  companion object {
    fun debug(msg: String) {
      if (false) {
        println(msg)
      }
    }
  }

  private val inputs = node.map { node ->
    debug("XX VariableInputsNode: node changed to $node")
    node.variables().toList().map { node.id to it }
  }

  init {
    node.addListener(ChangedListener { _ ->
      debug("XX VariableInputsNode: node changed to(2) ${node.value()}")
    })
//    node.onChange {
//      debug("XX VariableInputsNode: node changed to $it")
////      debug("XX VariableInputsNode: should propagate to $inputs")
//    }
//    inputs.onChange {
//      debug("XX VariableInputsNode: inputs changed to $it")
//    }
  }

  override val root = vbox {
    children.syncFrom(inputs) { it ->
      debug("XX VariableInputsNode: syncFrom $it")
      val (id, v) = it!!
      when (v) {
        is Input.Variable<out Any> -> InNode(In.Value(id, v))
        is Input.List<out Any> -> InNode(In.List(id, v))
      }
    }
  }
}