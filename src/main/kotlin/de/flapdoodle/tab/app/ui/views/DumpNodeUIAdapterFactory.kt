package de.flapdoodle.tab.app.ui.views

import de.flapdoodle.tab.app.model.Node

object DumpNodeUIAdapterFactory : NodeUIAdapterFactory {
    override fun adapterOf(node: Node): NodeUIAdapter {
        return when (node) {
            is Node.Constants -> constantAdapter(node)
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
        override fun update(node: Node) {
            require(node is Node.Constants) {"wrong type $node"}
            println(node.values)
        }
    }

    class TableUIAdapter : NodeUIAdapter() {
        override fun update(node: Node) {
            require(node is Node.Table<*>) {"wrong type $node"}
            println(node.columns)
        }
    }

    class CalculationUIAdapter : NodeUIAdapter() {
        override fun update(node: Node) {
            require(node is Node.Calculated<*>) {"wrong type $node"}
            println(node.columns)
            println(node.values)
        }
    }
}