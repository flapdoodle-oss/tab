package de.flapdoodle.tab.ui.views.calculations

import de.flapdoodle.eval.core.Expression
import de.flapdoodle.kfx.controls.fields.ValidatingColoredTextField
import de.flapdoodle.kfx.controls.labels.ColoredLabel
import de.flapdoodle.kfx.converters.Converters
import de.flapdoodle.kfx.converters.ValidatingConverter
import de.flapdoodle.kfx.converters.ValueOrError
import de.flapdoodle.kfx.layout.grid.TableCell
import de.flapdoodle.kfx.layout.grid.WeightGridTable
import de.flapdoodle.kfx.logging.Logging
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.calculations.Calculation
import de.flapdoodle.tab.model.calculations.adapter.Eval
import de.flapdoodle.tab.model.change.ModelChange
import de.flapdoodle.tab.model.data.SingleValue
import de.flapdoodle.tab.ui.ModelChangeListener
import de.flapdoodle.tab.ui.converter.ValidatingExpressionConverter
import de.flapdoodle.tab.ui.resources.Buttons
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.views.colors.ColorDot
import javafx.beans.property.SimpleObjectProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.Background
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import java.util.*

class AbstractCalculationListPane<K : Comparable<K>, C : Calculation<K>>(
    val nodeId: Id<Node.Calculated<*>>,
    val label: String,
    val context: Labels.WithContext = Labels.with(AbstractCalculationListPane::class),
    val modelChangeListener: ModelChangeListener,
    val calculationsModel: SimpleObjectProperty<List<C>>,
    val onNewExpression: () -> Unit,
    val onChangeExpression: (C) -> Unit,
    val outputColor: (C) -> Color?,
    val inputColor: (String) -> Color?
) : VBox() {
    private val logger = Logging.logger(javaClass)

    private val nameColumn = WeightGridTable.Column<C>(
        weight = 1.0,
        cellFactory = {
            TableCell.with(Labels.label(it.name().long), { it.name().long }, Label::setText)
        }
    )

    private val colorColumn = WeightGridTable.Column<C>(
        weight = 0.0,
        cellFactory = {
            TableCell(ColorDot(outputColor(it) ?: Color.BLACK)) { c, v -> c.set(outputColor(v) ?: Color.BLACK) }
        })


    private val formulaColumn = WeightGridTable.Column<C>(
        weight = 50.0,
        cellFactory = { calculation ->
            val textField = ValidatingColoredTextField(
                converter = ValidatingExpressionConverter(),
                default = calculation.formula().expression(),
                mapColors = { expression: Expression?, t ->
                    if (expression!=null) {
                        val variables = expression.variables()

                        val list = variables.names().flatMap { name ->
                            val color = inputColor(name)
                            if (color!=null) {
                                variables.positionsOf(name).map { p ->
                                    ColoredLabel.Part(p, p+name.length, color)
                                }
                            } else {
                                emptyList<ColoredLabel.Part>()
                            }

                        }
                        list
                    }
                    else {
                        emptyList<ColoredLabel.Part>()
                    }
                }
            )

            TableCell.with<C, ValidatingColoredTextField<Expression>, C>(textField, { it }, { t, value ->
                logger.info { "update: $value" }
                t.onAction = EventHandler {
                    if (value != null) {
                        modelChangeListener.change(
                            ModelChange.ChangeFormula(
                                nodeId,
                                value.id,
                                value.name(),
                                requireNotNull(t.get()) {"expression not set"}
                            )
                        )
                    }
                }

                // HACK to force a change for color mapping
                t.set(null)
                t.set(value?.formula()?.expression())
            }).apply {
                updateCell(calculation)
            }
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
            TableCell.with<C, Button, EventHandler<ActionEvent>>(
                node = Buttons.change(context),
                mapper = { v ->
                    EventHandler { ev: ActionEvent ->
                        onChangeExpression(v)
                    }
                },
                update = Button::setOnAction
            ).apply {
                updateCell(calculation)
            }
        }
    )

    private val aggregationsPanel = WeightGridTable(
        model = calculationsModel,
        indexOf = { it.id },
        columns = listOf(
            colorColumn,
            nameColumn,
            WeightGridTable.Column(weight = 0.0, cellFactory = { TableCell(Labels.label("=")) }),
            formulaColumn,
            changeColumn,
            deleteColumn
        ),
        footerFactory = { values, columns ->
            val addButton = Buttons.add(context) {
                    onNewExpression()
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