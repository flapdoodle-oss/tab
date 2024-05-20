package de.flapdoodle.tab.ui.views

import de.flapdoodle.tab.ui.ModelChangeListener
import javafx.event.EventHandler
import javafx.scene.control.Button

object DefaultNodeUIAdapterFactory : NodeUIAdapterFactory {
    override fun adapterOf(node: de.flapdoodle.tab.model.Node, modelChangeListener: ModelChangeListener): NodeUIAdapter {
        return when (node) {
            is de.flapdoodle.tab.model.Node.Constants -> ConstantUIAdapter(node, modelChangeListener)
            is de.flapdoodle.tab.model.Node.Table<out Comparable<*>> -> TableUIAdapter(node, modelChangeListener)
            is de.flapdoodle.tab.model.Node.Calculated<out Comparable<*>> -> InlineCalculatedUIAdapter(node, modelChangeListener)
        }
    }

    private fun constantAdapter(node: de.flapdoodle.tab.model.Node.Constants): NodeUIAdapter {
        return ConstantUIAdapter()
    }

    private fun <K: Comparable<K>> tableAdapter(node: de.flapdoodle.tab.model.Node.Table<K>): NodeUIAdapter {
        return TableUIAdapter()
    }

    private fun <K: Comparable<K>> calculationAdapter(node: de.flapdoodle.tab.model.Node.Calculated<K>): NodeUIAdapter {
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
        override fun update(node: de.flapdoodle.tab.model.Node) {
            require(node is de.flapdoodle.tab.model.Node.Constants) {"wrong type $node"}
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
        override fun update(node: de.flapdoodle.tab.model.Node) {
            require(node is de.flapdoodle.tab.model.Node.Table<*>) {"wrong type $node"}
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
        override fun update(node: de.flapdoodle.tab.model.Node) {
            require(node is de.flapdoodle.tab.model.Node.Calculated<*>) {"wrong type $node"}
            println(node.columns)
            println(node.values)
        }
    }
}