package de.flapdoodle.tab.ui.views.dialogs

import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.change.ModelChange

class Change {

    companion object {
        fun openWith(node: Node): ModelChange? {
            if (node is Node.Constants) {
                return DialogWrapper.open { ChangeValues(node) }
            }
            if (node is Node.Table<out Comparable<*>>) {
                return DialogWrapper.open { ChangeTable(node) }
            }
            return null
        }
    }
}