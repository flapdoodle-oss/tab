package de.flapdoodle.tab.ui.views.calculations

import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.tab.model.calculations.Calculation
import de.flapdoodle.tab.model.change.ModelChange
import de.flapdoodle.tab.ui.ModelChangeListener
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.views.dialogs.ChangeExpression
import de.flapdoodle.tab.ui.views.dialogs.NewExpression
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.layout.VBox

class CalculationsPane<K: Comparable<K>>(
    node: de.flapdoodle.tab.model.Node.Calculated<K>,
    val modelChangeListener: ModelChangeListener,
    val context: Labels.WithContext = Labels.with(CalculationsPane::class)
) : VBox() {
    private var currentNode = node
    private val nodeId = currentNode.id

    private val aggregationsPane = AbstractCalculationListPane(
        nodeId = currentNode.id,
        label = context.text("title.aggregations","Aggregations"),
        context = context,
        modelChangeListener = modelChangeListener,
        calculationsModel = SimpleObjectProperty(currentNode.calculations.aggregations()),
        onNewExpression = {
            val newExpression = NewExpression.open()
            if (newExpression!=null) {
                modelChangeListener.change(
                    ModelChange.AddAggregation(
                        nodeId,
                        newExpression.name,
                        newExpression.expression
                    )
                )
            }
        },
        onChangeExpression = { calculation: Calculation.Aggregation<K> ->
            val changedExpression = ChangeExpression.open(calculation.name(), calculation.formula().expression())
            if (changedExpression != null) {
                modelChangeListener.change(ModelChange.ChangeFormula(nodeId, calculation.id, changedExpression.name, changedExpression.expression))
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
        modelChangeListener = modelChangeListener,
        calculationsModel = SimpleObjectProperty(currentNode.calculations.tabular()),
        onNewExpression = {
            val newExpression = NewExpression.open()
            if (newExpression!=null) {
                modelChangeListener.change(
                    ModelChange.AddTabular(
                        nodeId,
                        newExpression.name,
                        newExpression.expression
                    )
                )
            }
        },
        onChangeExpression = { calculation: Calculation.Tabular<K> ->
            val changedExpression = ChangeExpression.open(calculation.name(), calculation.formula().expression())
            if (changedExpression != null) {
                modelChangeListener.change(ModelChange.ChangeFormula(nodeId, calculation.id, changedExpression.name, changedExpression.expression))
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