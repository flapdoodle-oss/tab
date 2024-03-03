package de.flapdoodle.tab.app.ui.views

import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.ui.ModelChangeListener
import de.flapdoodle.tab.app.ui.views.table.ColumnsPane
import de.flapdoodle.tab.app.ui.views.table.TablePane

class InlineTableUIAdapter<K: Comparable<K>>(
    node: Node.Table<K>,
    val modelChangeListener: ModelChangeListener
) : NodeUIAdapter() {
    val nodeId = node.id

    private val columnsPane = ColumnsPane(node, modelChangeListener)
    private val tablePane = TablePane(node, modelChangeListener)

    init {
        children.add(columnsPane)
        children.add(tablePane)
    }

    override fun update(node: Node) {
        require(node is Node.Table<out Comparable<*>>) { "wrong type $node" }
        require(node.id == nodeId) {"wrong node: ${node.id} != $nodeId"}

        columnsPane.update(node as Node.Table<K>)
        tablePane.update(node)
    }
}