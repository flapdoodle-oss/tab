package de.flapdoodle.tab.ui.views.calculations

import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.calculations.Calculation
import de.flapdoodle.tab.model.changes.Change
import de.flapdoodle.tab.ui.ChangeListener
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
    private var currentNode = node
    private val nodeId = currentNode.id

    private val aggregationsPane = AbstractCalculationListPane(
        nodeId = currentNode.id,
        label = context.text("title.aggregations","Aggregations"),
        context = context,
        changeListener = changeListener,
        calculationsModel = SimpleObjectProperty(currentNode.calculations.aggregations()),
        onNewExpression = {
            val newExpression = NewAggregationExpression.open()
            if (newExpression!=null) {
                changeListener.change(
                    Change.Calculation.AddAggregation(
                        nodeId,
                        newExpression.name,
                        newExpression.expression
                    )
                )
            }
        },
        onChangeExpression = { calculation: Calculation.Aggregation<K> ->
            val changedExpression = ChangeAggregationExpression.open(calculation.name(), calculation.formula().expression())
            if (changedExpression != null) {
                changeListener.change(Change.Calculation.ChangeAggregation(nodeId, calculation.id, changedExpression.name, changedExpression.expression))
            }
        },
        outputColor = {
            currentNode.findValue(it.destination())?.color
        },
        inputColor = { name ->
            currentNode.calculations.inputs().firstOrNull { it.name == name }?.color
        }
    )
    private val tabularPane = AbstractCalculationListPane(
        nodeId = currentNode.id,
        label = context.text("title.tabular","Tabular"),
        context = context,
        changeListener = changeListener,
        calculationsModel = SimpleObjectProperty(currentNode.calculations.tabular()),
        onNewExpression = {
            val newExpression = NewTabularExpression.open()
            if (newExpression!=null) {
                changeListener.change(
                    Change.Calculation.AddTabular(
                        nodeId,
                        newExpression.name,
                        newExpression.expression,
                        newExpression.interpolationType
                    )
                )
            }
        },
        onChangeExpression = { calculation: Calculation.Tabular<K> ->
            val changedExpression = ChangeTabularExpression.open(calculation.name(), calculation.formula().expression(), calculation.interpolationType())
            if (changedExpression != null) {
                changeListener.change(Change.Calculation.ChangeTabular(nodeId, calculation.id, changedExpression.name, changedExpression.expression, changedExpression.interpolationType))
            }
        },
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

    fun update(node: de.flapdoodle.tab.model.Node.Calculated<K>) {
        currentNode = node
        aggregationsPane.update(node.calculations.aggregations())
        tabularPane.update(node.calculations.tabular())
    }
}