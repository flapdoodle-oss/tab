package de.flapdoodle.tab.ui.views.calculations

import de.flapdoodle.eval.core.Expression
import de.flapdoodle.kfx.controls.fields.ValidatingColoredTextField
import de.flapdoodle.kfx.controls.labels.ColoredLabel
import de.flapdoodle.kfx.layout.grid.TableCell
import de.flapdoodle.kfx.layout.grid.WeightGridTable
import de.flapdoodle.kfx.logging.Logging
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.calculations.Calculation
import de.flapdoodle.tab.model.changes.Change
import de.flapdoodle.tab.ui.ChangeListener
import de.flapdoodle.tab.ui.converter.ValidatingExpressionConverter
import de.flapdoodle.tab.ui.resources.Buttons
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.views.colors.ColorDot
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.scene.paint.Color

class AbstractCalculationListPane<K : Comparable<K>, C : Calculation<K>>(
    val nodeId: Id<Node.Calculated<*>>,
    val label: String,
    val context: Labels.WithContext = Labels.with(AbstractCalculationListPane::class),
    val changeListener: ChangeListener,
    val calculationsModel: SimpleObjectProperty<List<C>>,
    val onNewExpression: () -> Unit,
    val onChangeExpression: (C) -> Unit,
    val outputColor: (C) -> Color?,
    val inputColor: (String) -> Color?
) : VBox() {
    private val logger = Logging.logger(javaClass)

    private val nameColumn = WeightGridTable.Column<C>(
        weight = 1.0,
        cellFactory = { c ->
            Labels.tableCell(c) { it.name().long }
        }
    )

    private val colorColumn = WeightGridTable.Column<C>(
        weight = 0.0,
        cellFactory = { c ->
            ColorDot.tableCell(c) { outputColor(it)}
        })


    private val formulaColumn = WeightGridTable.Column<C>(
        weight = 50.0,
        cellFactory = { calculation ->
            val textField = ValidatingColoredTextField(
                converter = ValidatingExpressionConverter(),
                default = calculation.formula().expression(),
                mapColors = { expression: Expression?, t ->
                    if (expression != null) {
                        val variables = expression.variables()

                        val list = variables.names().flatMap { name ->
                            val color = inputColor(name)
                            if (color != null) {
                                variables.positionsOf(name).map { p ->
                                    ColoredLabel.Part(p, p + name.length, color)
                                }
                            } else {
                                emptyList<ColoredLabel.Part>()
                            }

                        }
                        list
                    } else {
                        emptyList<ColoredLabel.Part>()
                    }
                }
            )

            TableCell.with<C, ValidatingColoredTextField<Expression>, C>(textField, { it }, { t, value ->
                logger.info { "update: $value" }
                t.onAction = EventHandler {
                    if (value != null) {
                        changeListener.change(
                            Change.Calculation.ChangeFormula(
                                nodeId,
                                value.id,
                                requireNotNull(t.get()) { "expression not set" }
                            )
                        )
                    }
                }

                // HACK to force a change for color mapping
                t.set(null)
                t.set(value?.formula()?.expression())
            }).initializedWith(calculation)
        }
    )

    private val deleteColumn = WeightGridTable.Column<C>(
        weight = 1.0,
        cellFactory = { calculation ->
            Buttons.tableCell(calculation, Buttons.delete(context)) {
                changeListener.change(Change.Calculation.RemoveFormula(nodeId, it.id))
            }
        }
    )

    private val changeColumn = WeightGridTable.Column<C>(
        weight = 1.0,
        cellFactory = { calculation ->
            Buttons.tableCell(calculation, Buttons.change(context)) {
                onChangeExpression(it)
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