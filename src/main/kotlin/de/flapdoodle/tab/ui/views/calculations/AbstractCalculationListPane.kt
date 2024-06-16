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
import javafx.beans.property.SimpleObjectProperty
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

    private val nameColumn = Columns.nameColumn<K, C>()
    private val colorColumn = Columns.colorColumn(outputColor)

    private val formulaColumn = Columns.formulaColumn<K, C>(inputColor) { value, expression ->
        changeListener.change(
            Change.Calculation.ChangeFormula(
                nodeId,
                value.id,
                expression
            )
        )
    }

    private val deleteColumn = Columns.deleteColumn<K, C>(context) {
        changeListener.change(Change.Calculation.RemoveFormula(nodeId, it.id))
    }

    private val changeColumn = Columns.changeColumn<K, C>(context) {
        onChangeExpression(it)
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