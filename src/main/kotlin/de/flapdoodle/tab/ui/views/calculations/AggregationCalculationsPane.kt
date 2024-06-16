package de.flapdoodle.tab.ui.views.calculations

import de.flapdoodle.kfx.layout.grid.TableCell
import de.flapdoodle.kfx.layout.grid.WeightGridTable
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.calculations.Calculation
import de.flapdoodle.tab.model.changes.Change
import de.flapdoodle.tab.ui.ChangeListener
import de.flapdoodle.tab.ui.resources.Buttons
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.views.dialogs.ChangeAggregationExpression
import de.flapdoodle.tab.ui.views.dialogs.NewAggregationExpression
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.scene.paint.Color

class AggregationCalculationsPane<K : Comparable<K>>(
    val nodeId: Id<Node.Calculated<*>>,
    val label: String,
    val context: Labels.WithContext = Labels.with(AggregationCalculationsPane::class),
    val changeListener: ChangeListener,
    val calculationsModel: ReadOnlyObjectProperty<List<Calculation.Aggregation<K>>>,
    val outputColor: (Calculation.Aggregation<K>) -> Color?,
    val inputColor: (String) -> Color?
) : VBox() {

    private val nameColumn = Columns.nameColumn<K, Calculation.Aggregation<K>>()
    private val colorColumn = Columns.colorColumn(outputColor)

    private val formulaColumn = Columns.formulaColumn<K, Calculation.Aggregation<K>>(inputColor) { value, expression ->
        changeListener.change(
            Change.Calculation.ChangeFormula(
                nodeId,
                value.id,
                expression
            )
        )
    }

    private val deleteColumn = Columns.deleteColumn<K, Calculation.Aggregation<K>>(context) {
        changeListener.change(Change.Calculation.RemoveFormula(nodeId, it.id))
    }

    private val changeColumn = Columns.changeColumn<K, Calculation.Aggregation<K>>(context) { calculation ->
        val changedExpression = ChangeAggregationExpression.open(calculation.name(), calculation.formula().expression())
        if (changedExpression != null) {
            changeListener.change(Change.Calculation.ChangeAggregation(nodeId, calculation.id, changedExpression.name, changedExpression.expression))
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
            changeColumn,
            deleteColumn
        ),
        footerFactory = { values, columns ->
            val addButton = Buttons.add(context) {
                val newExpression = NewAggregationExpression.open()
                if (newExpression!=null) {
                    changeListener.change(
                        Change.Calculation.AddAggregation(
                            nodeId,
                            newExpression.name,
                            newExpression.expression
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