package de.flapdoodle.tab.app.ui.views

import de.flapdoodle.tab.app.model.Node

interface NodeUIAdapterFactory {
    fun adapterOf(node: Node): NodeUIAdapter
}
