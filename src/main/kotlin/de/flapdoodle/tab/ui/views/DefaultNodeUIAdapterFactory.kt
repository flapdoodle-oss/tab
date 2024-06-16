package de.flapdoodle.tab.ui.views

import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.ui.ChangeListener

object DefaultNodeUIAdapterFactory : NodeUIAdapterFactory {
    override fun adapterOf(node: Node, changeListener: ChangeListener): NodeUIAdapter {
        return when (node) {
            is Node.Constants -> ConstantUIAdapter(node, changeListener)
            is Node.Table<out Comparable<*>> -> TableUIAdapter(node, changeListener)
            is Node.Calculated<out Comparable<*>> -> CalculatedUIAdapter(node, changeListener)
        }
    }

}