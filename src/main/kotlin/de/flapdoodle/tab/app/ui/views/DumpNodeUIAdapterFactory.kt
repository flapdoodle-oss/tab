package de.flapdoodle.tab.app.ui.views

import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.ui.ModelChangeListener
import javafx.event.EventHandler
import javafx.scene.control.Button

object DumpNodeUIAdapterFactory : NodeUIAdapterFactory {
    override fun adapterOf(node: Node, modelChangeListener: ModelChangeListener): NodeUIAdapter {
        return when (node) {
            is Node.Constants -> InlineConstantUIAdapter(node, modelChangeListener)
            is Node.Table<out Comparable<*>> -> tableAdapter(node)
            is Node.Calculated<out Comparable<*>> -> calculationAdapter(node)
        }
    }

    private fun constantAdapter(node: Node.Constants): NodeUIAdapter {
        return ConstantUIAdapter()
    }

    private fun <K: Comparable<K>> tableAdapter(node: Node.Table<K>): NodeUIAdapter {
        return TableUIAdapter()
    }

    private fun <K: Comparable<K>> calculationAdapter(node: Node.Calculated<K>): NodeUIAdapter {
        return CalculationUIAdapter()
    }

    class ConstantUIAdapter : NodeUIAdapter() {
        init {
            children.add(Button("clickMe").apply {
                onAction = EventHandler {
                    println("clicked")
                    it.consume()
                }
            })
        }
        override fun update(node: Node) {
            require(node is Node.Constants) {"wrong type $node"}
            println(node.values)
        }
    }

    class TableUIAdapter : NodeUIAdapter() {
        init {
            children.add(Button("clickMe").apply {
                onAction = EventHandler {
                    println("clicked")
                    it.consume()
                }
            })
        }
        override fun update(node: Node) {
            require(node is Node.Table<*>) {"wrong type $node"}
            println(node.columns)
        }
    }

    class CalculationUIAdapter : NodeUIAdapter() {
        init {
            children.add(Button("clickMe").apply {
                onAction = EventHandler {
                    println("clicked")
                    it.consume()
                }
            })
        }
        override fun update(node: Node) {
            require(node is Node.Calculated<*>) {"wrong type $node"}
            println(node.columns)
            println(node.values)
        }
    }
}