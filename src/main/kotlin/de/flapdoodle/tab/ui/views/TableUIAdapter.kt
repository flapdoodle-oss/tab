package de.flapdoodle.tab.ui.views

import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.ui.ChangeListener
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.views.charts.SmallChartPane
import de.flapdoodle.tab.ui.views.common.DescriptionPane
import de.flapdoodle.tab.ui.views.table.ColumnsPane
import de.flapdoodle.tab.ui.views.table.TablePane
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox

class TableUIAdapter<K: Comparable<K>>(
    node: Node.Table<K>,
    val changeListener: ChangeListener
) : NodeUIAdapter() {
    val nodeId = node.id
    private val context = Labels.with(TableUIAdapter::class)

    private val description = DescriptionPane(node.name.description)

    private val wrapper = WeightGridPane().apply {
        rowWeights(0.0, 1.0)
    }

    private val columnsPane = ColumnsPane(node, changeListener).apply {
        WeightGridPane.setPosition(this, 0, 0)
    }
    private val tabPane = TabPane().apply {
        WeightGridPane.setPosition(this, 0, 1)
    }

    private val tablePane = TablePane(node, changeListener, context)
    private val chartPane = SmallChartPane(node)

    init {
        bindCss("table-ui")

        VBox.setVgrow(wrapper, Priority.ALWAYS)
        
        wrapper.children.add(columnsPane)
        wrapper.children.add(tabPane)
        tabPane.tabs.add(Tab(context.text("tab.table","Table"), tablePane).apply {
            isClosable = false
        })
        tabPane.tabs.add(Tab(context.text("tab.charts","Charts"), chartPane).apply {
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