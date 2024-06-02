package de.flapdoodle.tab.ui.views.dialogs

import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.change.ModelChange

class ChangeNode {

    companion object {
        fun openWith(node: Node): ModelChange? {
            return when (node) {
                is Node.Constants -> DialogWrapper.open { ChangeValues(node) }
                is Node.Table<out Comparable<*>> -> DialogWrapper.open { ChangeTable(node) }
                is Node.Calculated<out Comparable<*>> -> DialogWrapper.open { ChangeCalculation(node) }
            }
        }
    }
}