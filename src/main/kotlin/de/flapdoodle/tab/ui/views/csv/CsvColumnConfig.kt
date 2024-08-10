package de.flapdoodle.tab.ui.views.csv

import de.flapdoodle.kfx.controls.bettertable.Column
import de.flapdoodle.kfx.controls.bettertable.ColumnProperty
import de.flapdoodle.kfx.controls.bettertable.Table
import de.flapdoodle.kfx.controls.bettertable.TableChangeListener
import de.flapdoodle.kfx.controls.bettertable.events.ReadOnlyState
import de.flapdoodle.kfx.css.bindCss
import de.flapdoodle.kfx.layout.grid.*
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.ui.resources.Buttons
import de.flapdoodle.tab.ui.resources.Labels
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.scene.control.Label

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

    private val columnMappings = SimpleObjectProperty<List<ColumnMapping>>(emptyList())

    private val csvTable = Table(
        rows = csvRows,
        columns = csvColumns,
        stateFactory = { ReadOnlyState(it) },
        changeListener = TableChangeListener.readOnly()
    )

    val nameColumn = GridTable.Column<ColumnMapping>(weight = 1.0, cellFactory = {
        TableCell(
            node = Label(it.name)
        )
    })
    val typeColumn = GridTable.Column<ColumnMapping>(weight = 0.0, cellFactory = {
        TableCell(
            node = Label(it.name)
        )
    })

    private val columnMappingTable = GridTable<ColumnMapping, Id<ColumnMapping>>(
        model = columnMappings,
        indexOf = ColumnMapping::id,
        footerFactories = listOf(GridTable.HeaderFooterFactory { values, columns ->
            val newColumnMapping = Buttons.add(Labels.with(CsvColumnConfig::class))
            mapOf(newColumnMapping to GridTable.Span(nameColumn))
        }),
        columns = listOf(
            nameColumn,
            typeColumn
        )
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
        add(columnMappingTable, Pos(0, row, columnSpan = allColumns))
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

    data class ColumnMapping(
        val name: String,
        val id: Id<ColumnMapping> = Id.nextId(ColumnMapping::class),
    )

//    class ColumnsPane : GridTable
}