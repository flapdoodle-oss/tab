package de.flapdoodle.tab.app.ui.views

import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.ui.ModelChangeListener
import de.flapdoodle.tab.app.ui.views.calculations.CalculationsPane
import de.flapdoodle.tab.app.ui.views.calculations.ValuesPane
import de.flapdoodle.tab.app.ui.views.table.TableViewPane

class InlineCalculatedUIAdapter<K: Comparable<K>>(
    node: Node.Calculated<K>,
    val modelChangeListener: ModelChangeListener
) : NodeUIAdapter() {
    val nodeId = node.id

    private val wrapper = WeightGridPane().apply {
        setRowWeight(0,0.1)
        setRowWeight(1, 1.0)
        setRowWeight(2, 10.0)
    }

    val calculationsPane = CalculationsPane(node, modelChangeListener).apply {
        WeightGridPane.setPosition(this, 0, 0)
    }
    val valuesPane = ValuesPane(node).apply {
        WeightGridPane.setPosition(this, 0, 1)
    }

    val tableViewPane = TableViewPane(node).apply {
        WeightGridPane.setPosition(this, 0, 2)
    }

    init {
        children.add(wrapper)
        wrapper.children.add(calculationsPane)
        wrapper.children.add(valuesPane)
        wrapper.children.add(tableViewPane)
    }

    override fun update(node: Node) {
        require(node is Node.Calculated<out Comparable<*>>) { "wrong type $node" }
        require(node.id == nodeId) {"wrong node: ${node.id} != $nodeId"}

        calculationsPane.update(node as Node.Calculated<K>)
        valuesPane.update(node)
        tableViewPane.update(node)
    }
}