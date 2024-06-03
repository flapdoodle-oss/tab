package de.flapdoodle.tab.ui.views.calculations

import de.flapdoodle.kfx.controls.fields.ValidatingColoredTextField
import de.flapdoodle.kfx.converters.Converters
import de.flapdoodle.kfx.layout.grid.TableCell
import de.flapdoodle.kfx.layout.grid.WeightGridTable
import de.flapdoodle.tab.model.calculations.Calculation
import de.flapdoodle.tab.model.change.ModelChange
import de.flapdoodle.tab.model.data.SingleValue
import de.flapdoodle.tab.ui.ModelChangeListener
import de.flapdoodle.tab.ui.resources.Buttons
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.views.dialogs.ChangeValue
import javafx.beans.property.SimpleObjectProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import java.util.*

class AbstractCalculationListPane<K: Comparable<K>, C: Calculation<K>>(
    node: de.flapdoodle.tab.model.Node.Calculated<K>,
    val label: String,
    val context: Labels.WithContext = Labels.with(AbstractCalculationListPane::class),
    val modelChangeListener: ModelChangeListener,
    val calculationsModel: SimpleObjectProperty<List<C>>,
    val onNewExpression: () -> Unit,
    val onChangeExpression: (C) -> Unit,
): VBox() {
    private val nodeId = node.id

    private val nameColumn = WeightGridTable.Column<C>(
        weight = 1.0,
        cellFactory = {
            TableCell.with(Labels.label(it.name().long), { it.name().long }, Label::setText)
        }
    )

    private val formulaColumn = WeightGridTable.Column<C>(
        weight = 50.0,
        cellFactory = { aggregation ->
            textFieldFor(aggregation, modelChangeListener)
        }
    )

    private val deleteColumn = WeightGridTable.Column<C>(
        weight = 1.0,
        cellFactory = { calculation ->
            TableCell(Buttons.delete(context) {
                modelChangeListener.change(ModelChange.RemoveFormula(nodeId, calculation.id))
            })
        }
    )

    private val changeColumn = WeightGridTable.Column<C>(
        weight = 1.0,
        cellFactory = { calculation ->
            TableCell.with<C, Button, EventHandler<ActionEvent>>(Buttons.change(context), { v -> EventHandler { ev: ActionEvent ->
                onChangeExpression(calculation)
            }}, Button::setOnAction).apply {
                updateCell(calculation)
            }
        }
    )

    private fun coloredTextFieldFor(calculation: C, modelChangeListener: ModelChangeListener): TableCell<C, ValidatingColoredTextField<String>> {
        val textField = ValidatingColoredTextField(Converters.validatingFor(String::class, Locale.GERMANY)).apply {
            onAction = EventHandler {
                val text = get()
                if (text != null) {
                    modelChangeListener.change(ModelChange.ChangeFormula(nodeId, calculation.id, text))
                }
            }
        }
        return TableCell(textField) { t, v ->
            t.set(v.formula().expression())
        }
    }

    private fun textFieldFor(calculation: C, modelChangeListener: ModelChangeListener): TableCell<C, TextField> {
        val textField = TextField(calculation.formula().expression()).apply {
            onAction = EventHandler {
                modelChangeListener.change(ModelChange.ChangeFormula(nodeId, calculation.id, text))
            }
        }

        return TableCell(textField) { t, v -> t.text = v.formula().expression() }
    }

    private val aggregationsPanel = WeightGridTable(
        model = calculationsModel,
        indexOf = { it.id },
        columns = listOf(
            nameColumn,
            WeightGridTable.Column(weight = 0.0, cellFactory = { TableCell( Labels.label("=")) }),
            formulaColumn,
            changeColumn,
            deleteColumn
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
            mapOf(deleteColumn to addButton)
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