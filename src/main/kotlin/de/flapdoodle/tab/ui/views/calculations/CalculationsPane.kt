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
    private val nodeId = node.id

    private val aggregationsPane = AbstractCalculationListPane(
        node = node,
        label = context.text("title.aggregations","Aggregations"),
        context = context,
        modelChangeListener = modelChangeListener,
        calculationsModel = SimpleObjectProperty(node.calculations.aggregations()),
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
            node.findValue(it.destination())?.color
        }
    )
    private val tabularPane = AbstractCalculationListPane(
        node = node,
        label = context.text("title.tabular","Tabular"),
        context = context,
        modelChangeListener = modelChangeListener,
        calculationsModel = SimpleObjectProperty(node.calculations.tabular()),
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
            node.findColumn(it.destination())?.color
        }
    )

    init {
        bindCss("calculations")
        children.add(aggregationsPane)
        children.add(tabularPane)
    }

    fun update(node: de.flapdoodle.tab.model.Node.Calculated<K>) {
        aggregationsPane.update(node.calculations.aggregations())
        tabularPane.update(node.calculations.tabular())
    }
}