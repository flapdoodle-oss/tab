package de.flapdoodle.tab.ui.views

import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.ui.ChangeListener

interface NodeUIAdapterFactory {
    fun adapterOf(node: Node, changeListener: ChangeListener): NodeUIAdapter
}
