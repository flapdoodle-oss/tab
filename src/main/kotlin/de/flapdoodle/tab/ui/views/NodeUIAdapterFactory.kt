package de.flapdoodle.tab.ui.views

import de.flapdoodle.tab.ui.ChangeListener
import de.flapdoodle.tab.ui.ModelChangeListener

interface NodeUIAdapterFactory {
    fun adapterOf(node: de.flapdoodle.tab.model.Node, modelChangeListener: ModelChangeListener, changeListener: ChangeListener): NodeUIAdapter
}
