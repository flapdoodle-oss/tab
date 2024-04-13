package de.flapdoodle.tab.app.ui.views

import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.ui.ModelChangeListener
import de.flapdoodle.tab.app.ui.views.calculations.CalculationsPane
import de.flapdoodle.tab.app.ui.views.calculations.ValuesPane
import de.flapdoodle.tab.app.ui.views.charts.SmallChartPane
import de.flapdoodle.tab.app.ui.views.table.TableViewPane
import javafx.scene.control.Tab
import javafx.scene.control.TabPane

class InlineCalculatedUIAdapter<K: Comparable<K>>(
    node: Node.Calculated<K>,
    val modelChangeListener: ModelChangeListener
) : NodeUIAdapter() {
    val nodeId = node.id

    private val wrapper = WeightGridPane().apply {
        setRowWeight(0,0.0)
        setRowWeight(1, 0.0)
        setRowWeight(2, 10.0)
//        setRowWeight(3, 10.0)
    }

    val calculationsPane = CalculationsPane(node, modelChangeListener).apply {
        WeightGridPane.setPosition(this, 0, 0)
    }
    val valuesPane = ValuesPane(node).apply {
        WeightGridPane.setPosition(this, 0, 1)
    }
    val tabPane = TabPane().apply {
        WeightGridPane.setPosition(this, 0, 2)
    }

    val tableViewPane = TableViewPane(node).apply {
        WeightGridPane.setPosition(this, 0, 3)
    }

    val chartPane = SmallChartPane(node)

    init {
        children.add(wrapper)
        wrapper.children.add(calculationsPane)
        wrapper.children.add(valuesPane)
        wrapper.children.add(tabPane)
        tabPane.tabs.add(Tab("Table", tableViewPane).apply {
            isClosable = false
        })
        tabPane.tabs.add(Tab("Chart", chartPane).apply {
            isClosable = false
        })
//        wrapper.children.add(tableViewPane)
    }

    override fun update(node: Node) {
        require(node is Node.Calculated<out Comparable<*>>) { "wrong type $node" }
        require(node.id == nodeId) {"wrong node: ${node.id} != $nodeId"}

        calculationsPane.update(node as Node.Calculated<K>)
        valuesPane.update(node)
        tableViewPane.update(node)
        chartPane.update(node)
    }
}