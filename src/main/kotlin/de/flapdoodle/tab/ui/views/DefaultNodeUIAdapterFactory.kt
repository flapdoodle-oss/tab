package de.flapdoodle.tab.ui.views

import de.flapdoodle.tab.ui.ChangeListener
import de.flapdoodle.tab.ui.ModelChangeListener

object DefaultNodeUIAdapterFactory : NodeUIAdapterFactory {
    override fun adapterOf(node: de.flapdoodle.tab.model.Node, modelChangeListener: ModelChangeListener, changeListener: ChangeListener): NodeUIAdapter {
        return when (node) {
            is de.flapdoodle.tab.model.Node.Constants -> ConstantUIAdapter(node, modelChangeListener, changeListener)
            is de.flapdoodle.tab.model.Node.Table<out Comparable<*>> -> TableUIAdapter(node, changeListener)
            is de.flapdoodle.tab.model.Node.Calculated<out Comparable<*>> -> CalculatedUIAdapter(node, modelChangeListener)
        }
    }

}