package de.flapdoodle.tab.ui.views.dialogs

import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.changes.Change

class ChangeNode {

    companion object {
        fun openWith(node: Node): Change? {
            return when (node) {
                is Node.Constants -> ChangeValues.open(node)
                is Node.Table<out Comparable<*>> -> ChangeTable.open(node)
                is Node.Calculated<out Comparable<*>> -> ChangeCalculated.open(node)
            }
        }
    }
}