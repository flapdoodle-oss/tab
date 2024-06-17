package de.flapdoodle.tab.ui.views.calculations

import de.flapdoodle.kfx.layout.grid.TableCell
import de.flapdoodle.kfx.layout.grid.WeightGridTable
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.calculations.Calculation
import de.flapdoodle.tab.model.calculations.interpolation.InterpolationType
import de.flapdoodle.tab.model.changes.Change
import de.flapdoodle.tab.model.data.Column
import de.flapdoodle.tab.ui.ChangeListener
import de.flapdoodle.tab.ui.resources.Buttons
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.views.dialogs.ChangeTabularExpression
import de.flapdoodle.tab.ui.views.dialogs.NewTabularExpression
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.scene.paint.Color

class TabularCalculationsPane<K : Comparable<K>>(
    val nodeId: Id<Node.Calculated<*>>,
    val label: String,
    val context: Labels.WithContext = Labels.with(TabularCalculationsPane::class),
    val changeListener: ChangeListener,
    val calculationsModel: ReadOnlyObjectProperty<List<Calculation.Tabular<K>>>,
    val outputColor: (Calculation.Tabular<K>) -> Color?,
    val inputColor: (String) -> Color?
) : VBox() {

    private val nameColumn = Columns.nameColumn<K, Calculation.Tabular<K>>()
    private val colorColumn = Columns.colorColumn(outputColor)

    private val formulaColumn = Columns.formulaColumn<K, Calculation.Tabular<K>>(inputColor) { value, expression ->
        changeListener.change(
            Change.Calculation.ChangeFormula(
                nodeId,
                value.id,
                expression
            )
        )
    }

    private val interpolationColumn = WeightGridTable.Column<Calculation.Tabular<K>>(weight = 0.0, cellFactory = {
        Labels.enumTableCell(it, InterpolationType::class, Calculation.Tabular<K>::interpolationType)
    })

    private val deleteColumn = Columns.deleteColumn<K, Calculation.Tabular<K>>(context) {
        changeListener.change(Change.Calculation.RemoveFormula(nodeId, it.id))
    }

    private val changeColumn = Columns.changeColumn<K, Calculation.Tabular<K>>(context) { calculation ->
        val changedExpression = ChangeTabularExpression.open(calculation.name(), calculation.formula().expression(), calculation.color(), calculation.interpolationType())
        if (changedExpression != null) {
            changeListener.change(Change.Calculation.ChangeTabular(nodeId, calculation.id, changedExpression.name, changedExpression.expression, changedExpression.color, changedExpression.interpolationType))
        }
    }

    private val aggregationsPanel = WeightGridTable(
        model = calculationsModel,
        indexOf = { it.id },
        columns = listOf(
            colorColumn,
            nameColumn,
            WeightGridTable.Column(weight = 0.0, cellFactory = { TableCell(Labels.label("=")) }),
            formulaColumn,
            interpolationColumn,
            changeColumn,
            deleteColumn
        ),
        footerFactory = { values, columns ->
            val addButton = Buttons.add(context) {
                val newExpression = NewTabularExpression.open()
                if (newExpression!=null) {
                    changeListener.change(
                        Change.Calculation.AddTabular(
                            nodeId,
                            newExpression.name,
                            newExpression.expression,
                            newExpression.color,
                            newExpression.interpolationType
                        )
                    )
                }
            }
            mapOf(deleteColumn to addButton)
        }
    )

    init {
        children.add(Label(label))
        children.add(aggregationsPanel)
    }
}