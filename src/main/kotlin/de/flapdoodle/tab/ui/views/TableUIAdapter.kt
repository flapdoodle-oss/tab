package de.flapdoodle.tab.ui.views

import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.tab.ui.ModelChangeListener
import de.flapdoodle.tab.ui.views.charts.SmallChartPane
import de.flapdoodle.tab.ui.views.common.DescriptionPane
import de.flapdoodle.tab.ui.views.table.ColumnsPane
import de.flapdoodle.tab.ui.views.table.TablePane
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.VBox

class TableUIAdapter<K: Comparable<K>>(
    node: de.flapdoodle.tab.model.Node.Table<K>,
    val modelChangeListener: ModelChangeListener
) : NodeUIAdapter() {
    val nodeId = node.id

    private val description = DescriptionPane(node.name.description)

    private val wrapper = WeightGridPane().apply {
        rowWeights(1.0, 0.0, 1.0)
    }

    private val columnsPane = ColumnsPane(node, modelChangeListener).apply {
        WeightGridPane.setPosition(this, 0, 1)
    }
    private val tabPane = TabPane().apply {
        WeightGridPane.setPosition(this, 0, 2)
    }
    private val tablePane = TablePane(node, modelChangeListener)
    private val chartPane = SmallChartPane(node)

    init {
        bindCss("table-ui")

        wrapper.children.add(description)
        wrapper.children.add(columnsPane)
        wrapper.children.add(tabPane)
        tabPane.tabs.add(Tab("Table", tablePane).apply {
            isClosable = false
        })
        tabPane.tabs.add(Tab("Chart", chartPane).apply {
            isClosable = false
        })
        tabPane.isVisible = node.columns.columns().isNotEmpty()
        children.add(VBox().also { vbox ->
            vbox.children.add(description)
            vbox.children.add(wrapper)
        })
    }

    override fun update(node: de.flapdoodle.tab.model.Node) {
        require(node is de.flapdoodle.tab.model.Node.Table<out Comparable<*>>) { "wrong type $node" }
        require(node.id == nodeId) {"wrong node: ${node.id} != $nodeId"}


        columnsPane.update(node as de.flapdoodle.tab.model.Node.Table<K>)
        description.update(node.name.description)
        tablePane.update(node)
        chartPane.update(node)
        tabPane.isVisible = node.columns.columns().isNotEmpty()
    }
}