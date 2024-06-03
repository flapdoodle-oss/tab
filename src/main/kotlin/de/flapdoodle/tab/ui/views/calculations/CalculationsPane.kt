package de.flapdoodle.tab.ui.views.calculations

import de.flapdoodle.tab.model.change.ModelChange
import de.flapdoodle.tab.ui.ModelChangeListener
import de.flapdoodle.tab.ui.resources.Labels
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
        onChangeExpression = {

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
        onChangeExpression = {

        }
    )

    init {
        children.add(aggregationsPane)
        children.add(tabularPane)
    }

    fun update(node: de.flapdoodle.tab.model.Node.Calculated<K>) {
        aggregationsPane.update(node.calculations.aggregations())
        tabularPane.update(node.calculations.tabular())
    }
}