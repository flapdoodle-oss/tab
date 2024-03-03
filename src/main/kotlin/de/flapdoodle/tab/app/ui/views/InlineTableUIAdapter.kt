package de.flapdoodle.tab.app.ui.views

import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.ui.ModelChangeListener
import de.flapdoodle.tab.app.ui.views.table.ColumnsPane

class InlineTableUIAdapter<K: Comparable<K>>(
    node: Node.Table<K>,
    val modelChangeListener: ModelChangeListener
) : NodeUIAdapter() {
    val nodeId = node.id

//    val calculationsPane = CalculationsPane(node, modelChangeListener)
    private val columnsPane = ColumnsPane(node, modelChangeListener)

    init {
//        children.add(calculationsPane)
        children.add(columnsPane)
    }

    override fun update(node: Node) {
        require(node is Node.Table<out Comparable<*>>) { "wrong type $node" }
        require(node.id == nodeId) {"wrong node: ${node.id} != $nodeId"}

//        calculationsPane.update(node as Node.Calculated<K>)
        columnsPane.update(node as Node.Table<K>)
    }
}