package de.flapdoodle.tab.app.ui.views

import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.ui.ModelChangeListener

interface NodeUIAdapterFactory {
    fun adapterOf(node: Node, modelChangeListener: ModelChangeListener): NodeUIAdapter
}
