package de.flapdoodle.tab.app.ui.views

import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.ui.ModelChangeListener
import de.flapdoodle.tab.app.ui.views.table.ColumnsPane
import de.flapdoodle.tab.app.ui.views.table.SlimTablePane
import de.flapdoodle.tab.app.ui.views.table.TablePane

class InlineTableUIAdapter<K: Comparable<K>>(
    node: Node.Table<K>,
    val modelChangeListener: ModelChangeListener
) : NodeUIAdapter() {
    val nodeId = node.id

    private val wrapper = WeightGridPane().apply {
        setRowWeight(0,0.1)
        setRowWeight(1, 1.0)
    }
    private val columnsPane = ColumnsPane(node, modelChangeListener).apply {
        WeightGridPane.setPosition(this, 0, 0)
    }
    private val tablePane = TablePane(node, modelChangeListener).apply {
        WeightGridPane.setPosition(this, 0, 1)
    }

    init {
        children.add(wrapper)
        wrapper.children.add(columnsPane)
        wrapper.children.add(tablePane)
    }

    override fun update(node: Node) {
        require(node is Node.Table<out Comparable<*>>) { "wrong type $node" }
        require(node.id == nodeId) {"wrong node: ${node.id} != $nodeId"}

        columnsPane.update(node as Node.Table<K>)
        tablePane.update(node)
    }
}