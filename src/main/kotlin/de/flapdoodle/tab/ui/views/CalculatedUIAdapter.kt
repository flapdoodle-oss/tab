package de.flapdoodle.tab.ui.views

import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.tab.ui.ModelChangeListener
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.views.calculations.CalculationsPane
import de.flapdoodle.tab.ui.views.calculations.ValuesPane
import de.flapdoodle.tab.ui.views.charts.SmallChartPane
import de.flapdoodle.tab.ui.views.common.DescriptionPane
import de.flapdoodle.tab.ui.views.table.TableViewPane
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.VBox

class CalculatedUIAdapter<K: Comparable<K>>(
    node: de.flapdoodle.tab.model.Node.Calculated<K>,
    val modelChangeListener: ModelChangeListener
) : NodeUIAdapter() {
    val nodeId = node.id

    private val context = Labels.with(CalculatedUIAdapter::class)
    private val description = DescriptionPane(node.name.description)

    private val wrapper = WeightGridPane().apply {
        rowWeights(0.0, 0.0, 1.0)
    }

    val calculationsPane = CalculationsPane(node, modelChangeListener, context).apply {
        WeightGridPane.setPosition(this, 0, 0)
    }
    val valuesPane = ValuesPane(node).apply {
        WeightGridPane.setPosition(this, 0, 1)
    }
    val tabPane = TabPane().apply {
        WeightGridPane.setPosition(this, 0, 2)
    }

    val tableViewPane = TableViewPane(node, context).apply {
        WeightGridPane.setPosition(this, 0, 3)
    }

    val chartPane = SmallChartPane(node)

    init {
        bindCss("calculated-ui")
        wrapper.children.add(calculationsPane)
        wrapper.children.add(valuesPane)
        wrapper.children.add(tabPane)
        tabPane.tabs.add(Tab(context.text("tab.table","Table"), tableViewPane).apply {
            isClosable = false
        })
        tabPane.tabs.add(Tab(context.text("tab.charts","Charts"), chartPane).apply {
            isClosable = false
        })
//        wrapper.children.add(tableViewPane)
        children.add(VBox().also { vbox ->
            vbox.children.add(description)
            vbox.children.add(wrapper)
        })
    }

    override fun update(node: de.flapdoodle.tab.model.Node) {
        require(node is de.flapdoodle.tab.model.Node.Calculated<out Comparable<*>>) { "wrong type $node" }
        require(node.id == nodeId) {"wrong node: ${node.id} != $nodeId"}

        calculationsPane.update(node as de.flapdoodle.tab.model.Node.Calculated<K>)
        valuesPane.update(node)
        tableViewPane.update(node)
        chartPane.update(node)
        description.update(node.name.description)
    }
}