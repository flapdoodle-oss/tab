package de.flapdoodle.tab.ui.views.csv

import de.flapdoodle.kfx.controls.bettertable.Column
import de.flapdoodle.kfx.controls.bettertable.ColumnProperty
import de.flapdoodle.kfx.controls.bettertable.Table
import de.flapdoodle.kfx.controls.bettertable.TableChangeListener
import de.flapdoodle.kfx.controls.bettertable.events.ReadOnlyState
import de.flapdoodle.kfx.css.bindCss
import de.flapdoodle.kfx.dialogs.DialogContent
import de.flapdoodle.kfx.layout.grid.GridPane
import de.flapdoodle.kfx.layout.grid.Pos
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.Node
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import java.nio.file.Path

class CsvColumnConfig(
    val state: ImportCsvState?
) : AbstractCsvDialogStep<ImportCsvState>() {
    private val isValid = SimpleObjectProperty<Boolean>(false)
    private var current = requireNotNull(state) { "state is null"}

    private val csvColumns =  SimpleObjectProperty<List<Column<List<String>, out Any>>>(current.csvColumnNames.mapIndexed { column, label ->
        Column(label, ColumnProperty<List<String>, String>(
            type = TypeInfo.of(String::class.java),
            getter = { row -> if (row.size > column) row[column] else null }
        ), false)
    })
    private val csvRows = SimpleObjectProperty(current.csvRows)

    private val csvTable = Table(
        rows = csvRows,
        columns = csvColumns,
        stateFactory = { ReadOnlyState(it) },
        changeListener = TableChangeListener.readOnly()
    )

    init {
        bindCss("csv-column-config")
        columnWeights(0.0, 1.0)

        var row=0
        val allColumns = 2

        csvTable.prefWidth = 800.0
        csvTable.prefHeight = 200.0
        add(csvTable, Pos(0, row, columnSpan = allColumns))

        row++
//        add(CsvFormatPane(state.path), 0, 0)
    }

    override fun enter() {
        
    }

    override fun isValidProperty(): ObservableValue<Boolean> {
        return isValid
    }

    override fun result(): ImportCsvState {
        return current
    }
}