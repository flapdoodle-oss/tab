package de.flapdoodle.tab.ui.views

import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.tab.ui.ModelChangeListener
import de.flapdoodle.tab.ui.views.charts.SmallChartPane
import de.flapdoodle.tab.ui.views.table.ColumnsPane
import de.flapdoodle.tab.ui.views.table.TablePane
import javafx.scene.control.Tab
import javafx.scene.control.TabPane

class TableUIAdapter<K: Comparable<K>>(
    node: de.flapdoodle.tab.model.Node.Table<K>,
    val modelChangeListener: ModelChangeListener
) : NodeUIAdapter() {
    val nodeId = node.id

    private val wrapper = WeightGridPane().apply {
        rowWeights(0.0, 1.0)
    }
    private val columnsPane = ColumnsPane(node, modelChangeListener).apply {
        WeightGridPane.setPosition(this, 0, 0)
    }
    private val tabPane = TabPane().apply {
        WeightGridPane.setPosition(this, 0, 1)
    }
    private val tablePane = TablePane(node, modelChangeListener).apply {
        WeightGridPane.setPosition(this, 0, 1)
    }
    private val chartPane = SmallChartPane(node)

    init {
        children.add(wrapper)
        wrapper.children.add(columnsPane)
        wrapper.children.add(tabPane)
        tabPane.tabs.add(Tab("Table", tablePane).apply {
            isClosable = false
        })
        tabPane.tabs.add(Tab("Chart", chartPane).apply {
            isClosable = false
        })
        tabPane.isVisible = node.columns.columns().isNotEmpty()
    }

    override fun update(node: de.flapdoodle.tab.model.Node) {
        require(node is de.flapdoodle.tab.model.Node.Table<out Comparable<*>>) { "wrong type $node" }
        require(node.id == nodeId) {"wrong node: ${node.id} != $nodeId"}


        columnsPane.update(node as de.flapdoodle.tab.model.Node.Table<K>)
        tablePane.update(node)
        chartPane.update(node)
        tabPane.isVisible = node.columns.columns().isNotEmpty()
    }
}