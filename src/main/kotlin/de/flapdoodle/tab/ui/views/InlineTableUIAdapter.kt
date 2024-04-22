package de.flapdoodle.tab.ui.views

import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.tab.ui.ModelChangeListener
import de.flapdoodle.tab.ui.views.table.ColumnsPane
import de.flapdoodle.tab.ui.views.table.TablePane

class InlineTableUIAdapter<K: Comparable<K>>(
    node: de.flapdoodle.tab.model.Node.Table<K>,
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

    override fun update(node: de.flapdoodle.tab.model.Node) {
        require(node is de.flapdoodle.tab.model.Node.Table<out Comparable<*>>) { "wrong type $node" }
        require(node.id == nodeId) {"wrong node: ${node.id} != $nodeId"}


        columnsPane.update(node as de.flapdoodle.tab.model.Node.Table<K>)
        tablePane.update(node)
    }
}