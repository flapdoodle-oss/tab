package de.flapdoodle.tab.ui.views.calculations

import de.flapdoodle.eval.core.Expression
import de.flapdoodle.eval.core.exceptions.BaseException
import de.flapdoodle.kfx.controls.Tooltips
import de.flapdoodle.kfx.controls.fields.ValidatingColoredTextField
import de.flapdoodle.kfx.controls.labels.ColoredLabel
import de.flapdoodle.kfx.layout.grid.TableCell
import de.flapdoodle.kfx.layout.grid.WeightGridTable
import de.flapdoodle.tab.model.calculations.Calculation
import de.flapdoodle.tab.model.changes.Change
import de.flapdoodle.tab.ui.converter.ValidatingExpressionConverter
import de.flapdoodle.tab.ui.resources.Buttons
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.views.colors.ColorDot
import javafx.event.EventHandler
import javafx.geometry.HPos
import javafx.scene.paint.Color

object Columns {

    fun <K : Comparable<K>, C : Calculation<K>> nameColumn(weight: Double = 1.0) = WeightGridTable.Column<C>(
        weight = weight,
        horizontalPosition = HPos.RIGHT,
        cellFactory = { c -> Labels.tableCell(c) { it.name().long } }
    )

    fun <K : Comparable<K>, C : Calculation<K>> colorColumn(outputColor: (C) -> Color?, weight: Double = 0.0) = WeightGridTable.Column<C>(
        weight = weight,
        cellFactory = { c ->
            ColorDot.tableCell(c) {
                outputColor(it)
            }
        })

    fun <K : Comparable<K>, C : Calculation<K>> deleteColumn(context: Labels.WithContext, weight: Double = 1.0, onAction: (C) -> Unit) = WeightGridTable.Column<C>(
        weight = weight,
        cellFactory = { calculation ->
            Buttons.tableCell(calculation, Buttons.delete(context), onAction)
        }
    )

    fun <K : Comparable<K>, C : Calculation<K>> changeColumn(context: Labels.WithContext, weight: Double = 1.0, onAction: (C) -> Unit) = WeightGridTable.Column<C>(
        weight = weight,
        cellFactory = { calculation ->
            Buttons.tableCell(calculation, Buttons.change(context), onAction)
        }
    )

    fun <K : Comparable<K>, C : Calculation<K>>  formulaColumn(
        inputColor: (String) -> Color?,
        weight: Double = 50.0,
        onChange: (C, Expression) -> Unit
    ) = WeightGridTable.Column<C>(
        weight = weight,
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
                                emptyList()
                            }

                        }
                        list
                    } else {
                        emptyList()
                    }
                }
            )

            TableCell.initializedWith(calculation)
                .node(textField)
                .updateWith { validatingColoredTextField, value ->
                    validatingColoredTextField.onAction = EventHandler {
                        if (value != null) {
                            onChange(value, requireNotNull(validatingColoredTextField.get()) { "expression not set" })
                        }
                    }
                    // HACK to force a change for color mapping
                    validatingColoredTextField.set(null)
                    validatingColoredTextField.set(value?.formula()?.expression())

                    // TODO set external error
                    val error = value?.error()
                    validatingColoredTextField.tooltip = if (error!=null) Tooltips.tooltip(error.message) else null
                }
        }
    )

}