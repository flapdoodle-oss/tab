package de.flapdoodle.tab.ui.views.calculations

import de.flapdoodle.tab.model.change.ModelChange
import de.flapdoodle.tab.ui.ModelChangeListener
import de.flapdoodle.tab.ui.views.dialogs.NewExpressionDialog
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.layout.VBox

class CalculationsPane<K: Comparable<K>>(
    node: de.flapdoodle.tab.model.Node.Calculated<K>,
    val modelChangeListener: ModelChangeListener
) : VBox() {
    private val nodeId = node.id
//    private val aggregationsModel = SimpleObjectProperty(node.calculations.aggregations())
//
//    private val nameColumn = WeightGridTable.Column<Calculation.Aggregation<K>>(
//        weight = 1.0,
//        nodeFactory = { aggregation ->
//            val label = Label(aggregation.name())
//            label to WeightGridTable.ChangeListener { label.text = it.name() }
//        }
//    )
//
//    private val formulaColumn = WeightGridTable.Column<Calculation.Aggregation<K>>(
//        weight = 50.0,
//        nodeFactory = { aggregation ->
//            textFieldFor(aggregation, modelChangeListener)
//        }
//    )
//
//    private val actionColumn = WeightGridTable.Column<Calculation.Aggregation<K>>(
//        weight = 1.0,
//        nodeFactory = { aggregation ->
//            val button = Button("-").apply {
//                onAction = EventHandler {
//                    val newExpression = NewExpressionDialog.open()
//                    println("new expression: $newExpression")
//                }
//            }
//            button to WeightGridTable.ChangeListener {}
//        }
//    )
//
//    private fun textFieldFor(aggregation: Calculation.Aggregation<K>, modelChangeListener: ModelChangeListener): Pair<javafx.scene.Node, WeightGridTable.ChangeListener<Calculation.Aggregation<K>>> {
//        val changeFormula = {  expression: String ->
//            modelChangeListener.change(ModelChange.ChangeFormula(nodeId, aggregation.id, expression))
//        }
//
//        val textField = TextField(aggregation.formula().expression()).apply {
//            onAction = EventHandler {
//                changeFormula(text)
//            }
//        }
//        val changeListener = WeightGridTable.ChangeListener<Calculation.Aggregation<K>> { change ->
//            textField.textProperty().value = change.formula().expression()
//        }
//
//        return textField to changeListener
//    }
//
//    private val aggregationsPanel = WeightGridTable(
//        model = aggregationsModel,
//        indexOf = { it.id },
//        columns = listOf(
//            nameColumn,
//            WeightGridTable.Column(weight = 0.1, nodeFactory = { Label("=") to WeightGridTable.ChangeListener {  } }),
//            formulaColumn,
//            actionColumn
//        ),
//        footerFactory = { values, columns ->
//            val addButton = Button("+").apply {
//                onAction = EventHandler {
//                    val newExpression = NewExpressionDialog.open()
//                    if (newExpression!=null) {
//                        modelChangeListener.change(
//                            ModelChange.AddAggregation(
//                                nodeId,
//                                newExpression.name,
//                                newExpression.expression
//                            )
//                        )
//                    }
//                }
//            }
//            mapOf(actionColumn to addButton)
//        }
//    )
//
//
//    init {
//        children.add(aggregationsPanel)
//    }

    private val aggregationsPane = AbstractCalculationListPane(node, "Aggregations", modelChangeListener, SimpleObjectProperty(node.calculations.aggregations())) {
        val newExpression = NewExpressionDialog.open()
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
        val newExpression = NewExpressionDialog.open()
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