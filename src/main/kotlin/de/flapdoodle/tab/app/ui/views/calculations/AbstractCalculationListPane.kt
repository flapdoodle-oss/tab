package de.flapdoodle.tab.app.ui.views.calculations

import de.flapdoodle.kfx.layout.grid.WeightGridTable
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.calculations.Calculation
import de.flapdoodle.tab.app.model.change.ModelChange
import de.flapdoodle.tab.app.ui.ModelChangeListener
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.VBox

class AbstractCalculationListPane<K: Comparable<K>, C: Calculation<K>>(
    node: Node.Calculated<K>,
    val label: String,
    val modelChangeListener: ModelChangeListener,
    val calculationsModel: SimpleObjectProperty<List<C>>,
    val onNewExpression: () -> Unit
): VBox() {
    private val nodeId = node.id
//    private val calculationsModel: SimpleObjectProperty<List<Calculation.Aggregation<K>>> = SimpleObjectProperty(node.calculations.aggregations())

    private val nameColumn = WeightGridTable.Column<C>(
        weight = 1.0,
        nodeFactory = { aggregation ->
            val label = Label(aggregation.name())
            label to WeightGridTable.ChangeListener { label.text = it.name() }
        }
    )

    private val formulaColumn = WeightGridTable.Column<C>(
        weight = 50.0,
        nodeFactory = { aggregation ->
            textFieldFor(aggregation, modelChangeListener)
        }
    )

    private val actionColumn = WeightGridTable.Column<C>(
        weight = 1.0,
        nodeFactory = { calculation ->
            val button = Button("-").apply {
                onAction = EventHandler {
                    modelChangeListener.change(ModelChange.RemoveFormula(nodeId,calculation.id))
                }
            }
            button to WeightGridTable.ChangeListener {}
        }
    )

    private fun textFieldFor(calculation: C, modelChangeListener: ModelChangeListener): Pair<javafx.scene.Node, WeightGridTable.ChangeListener<C>> {
        val changeFormula = {  expression: String ->
            modelChangeListener.change(ModelChange.ChangeFormula(nodeId, calculation.id, expression))
        }

        val textField = TextField(calculation.formula().expression()).apply {
            onAction = EventHandler {
                changeFormula(text)
            }
        }
        val changeListener = WeightGridTable.ChangeListener<C> { change ->
            textField.textProperty().value = change.formula().expression()
        }

        return textField to changeListener
    }

    private val aggregationsPanel = WeightGridTable(
        model = calculationsModel,
        indexOf = { it.id },
        columns = listOf(
            nameColumn,
            WeightGridTable.Column(weight = 0.1, nodeFactory = { Label("=") to WeightGridTable.ChangeListener {  } }),
            formulaColumn,
            actionColumn
        ),
        footerFactory = { values, columns ->
            val addButton = Button("+").apply {
                onAction = EventHandler {
                    onNewExpression()
//                    val newExpression = NewExpressionDialog.open()
//                    if (newExpression!=null) {
//
//                        modelChangeListener.change(
//                            ModelChange.AddAggregation(
//                                nodeId,
//                                newExpression.name,
//                                newExpression.expression
//                            )
//                        )
//                    }
                }
            }
            mapOf(actionColumn to addButton)
        }
    )

    init {
        children.add(Label(label))
        children.add(aggregationsPanel)
    }

    fun update(list: List<C>) {
        calculationsModel.value = list
    }
}