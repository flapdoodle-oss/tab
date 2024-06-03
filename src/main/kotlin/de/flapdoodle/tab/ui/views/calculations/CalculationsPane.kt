package de.flapdoodle.tab.ui.views.calculations

import de.flapdoodle.tab.model.change.ModelChange
import de.flapdoodle.tab.ui.ModelChangeListener
import de.flapdoodle.tab.ui.views.dialogs.NewExpression
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.layout.VBox

class CalculationsPane<K: Comparable<K>>(
    node: de.flapdoodle.tab.model.Node.Calculated<K>,
    val modelChangeListener: ModelChangeListener
) : VBox() {
    private val nodeId = node.id

    private val aggregationsPane = AbstractCalculationListPane(node, "Aggregations", modelChangeListener, SimpleObjectProperty(node.calculations.aggregations())) {
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
    }
    private val tabularPane = AbstractCalculationListPane(node, "Tabular", modelChangeListener, SimpleObjectProperty(node.calculations.tabular())) {
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
    }

    init {
        children.add(aggregationsPane)
        children.add(tabularPane)
    }

    fun update(node: de.flapdoodle.tab.model.Node.Calculated<K>) {
        aggregationsPane.update(node.calculations.aggregations())
        tabularPane.update(node.calculations.tabular())
    }
}