package de.flapdoodle.tab.app.ui.views.calculations

import de.flapdoodle.kfx.layout.grid.WeightGridTable
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.calculations.Calculation
import de.flapdoodle.tab.app.model.change.ModelChange
import de.flapdoodle.tab.app.ui.ModelChangeListener
import de.flapdoodle.tab.app.ui.views.dialogs.NewExpressionDialog
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.layout.VBox
import javafx.stage.Modality

class CalculationsPane<K: Comparable<K>>(
    node: Node.Calculated<K>,
    val modelChangeListener: ModelChangeListener
) : VBox() {
    private val nodeId = node.id
    private val aggregationsModel = SimpleObjectProperty(node.calculations.aggregations())

    private val nameColumn = WeightGridTable.Column<Calculation.Aggregation<K>>(
        weight = 1.0,
        nodeFactory = { aggregation ->
            val label = Label(aggregation.name())
            label to WeightGridTable.ChangeListener { label.text = it.name() }
        }
    )

    private val formulaColumn = WeightGridTable.Column<Calculation.Aggregation<K>>(
        weight = 50.0,
        nodeFactory = { aggregation ->
            textFieldFor(aggregation, modelChangeListener)
        }
    )

    private val actionColumn = WeightGridTable.Column<Calculation.Aggregation<K>>(
        weight = 1.0,
        nodeFactory = { aggregation ->
//            val dialog = Dialog<ButtonType>().apply {
//                title = "X"
//                contentText = "What?"
//                dialogPane.buttonTypes.add(ButtonType.YES)
//                dialogPane.buttonTypes.add(ButtonType.CANCEL)
//            }
//            dialog.initModality(Modality.APPLICATION_MODAL);
//            dialog.dialogPane.
                    
            val button = Button("-").apply {
                onAction = EventHandler {
                    val newExpression = NewExpressionDialog.open()
                    println("new expression: $newExpression")
//                    modelChangeListener.change(ModelChange.RemoveFormula(nodeId, aggregation.id))
//                    dialog.showAndWait()
//                        .filter {
//                            println("_-> $it")
//                            true
//                        }
//                        .filter(response -> response == ButtonType.OK)
//                    .ifPresent(response -> formatSystem());
                }
            }
            button to WeightGridTable.ChangeListener {}
        }
    )

    private fun textFieldFor(aggregation: Calculation.Aggregation<K>, modelChangeListener: ModelChangeListener): Pair<javafx.scene.Node, WeightGridTable.ChangeListener<Calculation.Aggregation<K>>> {
        val changeFormula = {  expression: String ->
            modelChangeListener.change(ModelChange.ChangeFormula(nodeId, aggregation.id, expression))
        }

        val textField = TextField(aggregation.formula().expression()).apply {
            onAction = EventHandler {
                changeFormula(text)
            }
//            focusedProperty().addListener { observable, oldValue, newValue ->
//                if (!newValue) {
//                    changeFormula(text)
//                }
//            }
        }
        val changeListener = WeightGridTable.ChangeListener<Calculation.Aggregation<K>> { change ->
            textField.textProperty().value = change.formula().expression()
        }

        return textField to changeListener
    }

    private val aggregationsPanel = WeightGridTable(
        model = aggregationsModel,
        indexOf = { it.id },
        columns = listOf(
            nameColumn,
            WeightGridTable.Column(weight = 0.1, nodeFactory = { Label("=") to WeightGridTable.ChangeListener {  } }),
            formulaColumn,
            actionColumn
        ),
        footerFactory = { values, columns ->
//            val nameTextField = TextField("name").apply {
//                prefWidth = 20.0
//            }
//            val expressionTextField = TextField("43")
            val addButton = Button("+").apply {
                onAction = EventHandler {
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
            }
            mapOf(actionColumn to addButton)
        }
    )


    init {
        children.add(aggregationsPanel)
    }

    fun update(node: Node.Calculated<K>) {
        aggregationsModel.value = node.calculations.aggregations()
    }
}