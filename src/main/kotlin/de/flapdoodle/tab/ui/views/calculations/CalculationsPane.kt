package de.flapdoodle.tab.ui.views.calculations

import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.logging.Logging
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.calculations.Calculation
import de.flapdoodle.tab.model.changes.Change
import de.flapdoodle.tab.ui.ChangeListener
import de.flapdoodle.tab.ui.ModelAdapter
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.views.dialogs.ChangeAggregationExpression
import de.flapdoodle.tab.ui.views.dialogs.ChangeTabularExpression
import de.flapdoodle.tab.ui.views.dialogs.NewAggregationExpression
import de.flapdoodle.tab.ui.views.dialogs.NewTabularExpression
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.layout.VBox

class CalculationsPane<K: Comparable<K>>(
    node: Node.Calculated<K>,
    val changeListener: ChangeListener,
    val context: Labels.WithContext = Labels.with(CalculationsPane::class)
) : VBox() {
    private val logger = Logging.logger(CalculationsPane::class)

    private var currentNode = node

    private val aggregationModel = SimpleObjectProperty(currentNode.calculations.aggregations())
    private val aggregationsPane = AggregationCalculationsPane(
        nodeId = currentNode.id,
        label = context.text("title.aggregations","Aggregations"),
        context = context,
        changeListener = changeListener,
        calculationsModel = aggregationModel,
        outputColor = {
            logger.info { "find destination: ${currentNode.findValue(it.destination())}" }
            logger.info { "${currentNode.values}" }
            currentNode.findValue(it.destination())?.color
        },
        inputColor = { name ->
            currentNode.calculations.inputs().firstOrNull { it.name == name }?.color
        }
    )

    private val tabularModel = SimpleObjectProperty(currentNode.calculations.tabular())
    private val tabularPane = TabularCalculationsPane(
        nodeId = currentNode.id,
        label = context.text("title.tabular","Tabular"),
        context = context,
        changeListener = changeListener,
        calculationsModel = tabularModel,
        outputColor = {
            currentNode.findColumn(it.destination())?.color
        },
        inputColor = { name ->
            currentNode.calculations.inputs().firstOrNull { it.name == name }?.color
        }
    )

    init {
        bindCss("calculations")
        children.add(aggregationsPane)
        children.add(tabularPane)
    }

    fun update(node: Node.Calculated<K>) {
        require(currentNode.id == node.id) {"wrong node: $node"}
        currentNode = node

        aggregationModel.value = node.calculations.aggregations()
        tabularModel.value = node.calculations.tabular()
    }
}