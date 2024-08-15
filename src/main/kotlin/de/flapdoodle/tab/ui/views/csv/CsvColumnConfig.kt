package de.flapdoodle.tab.ui.views.csv

import de.flapdoodle.kfx.bindings.ObjectBindings
import de.flapdoodle.kfx.controls.bettertable.Column
import de.flapdoodle.kfx.controls.bettertable.ColumnProperty
import de.flapdoodle.kfx.controls.bettertable.Table
import de.flapdoodle.kfx.controls.bettertable.TableChangeListener
import de.flapdoodle.kfx.controls.bettertable.events.ReadOnlyState
import de.flapdoodle.kfx.controls.fields.ValidatingChoiceBox
import de.flapdoodle.kfx.controls.fields.ValidatingField
import de.flapdoodle.kfx.controls.fields.ValidatingTextField
import de.flapdoodle.kfx.css.bindCss
import de.flapdoodle.kfx.layout.grid.*
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.types.Strings
import de.flapdoodle.tab.ui.Converters
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

    private val mappedCsvColumns = SimpleObjectProperty<List<Column<Map<Int, Any?>, out Any>>>(emptyList())
    private val mappedCsvRows = SimpleObjectProperty<List<Map<Int, Any?>>>(emptyList())

    private fun <T: Any> mappedColumn(
        index: Int,
        mapping: ColumnMapping,
        converter: CsvCellConverter<T>
    ): Column<Map<Int, Any?>, T> {
        return Column(
            label = mapping.name,
            property = ColumnProperty(
                type = converter.type,
                getter = { row -> row[index] as T? }
            ),
            editable = false
        )
    }

    private fun mappingsAsConverter(mappings: List<ColumnMapping>): (List<String>) -> Map<Int, Any?> {
        val converterMap = mappings.map {
            it.index to it.converter
        }
        return { row ->
            converterMap.mapIndexed { index, (sourceIndex, converter) ->
                val sourceValue = row[sourceIndex]
                val converted = converter.conversion(sourceValue, null)
                index to converted
            }.toMap()
        }
    }

    init {
        mappedCsvColumns.bind(ObjectBindings.map(columnMappings) { mappings ->
            mappings.mapIndexed { index, it -> mappedColumn(index, it, it.converter) }
        })

        mappedCsvRows.bind(ObjectBindings.merge(csvRows, columnMappings) { rows, mappings ->
            val converter: (List<String>) -> Map<Int, Any?> = mappingsAsConverter(mappings)
            rows.map { converter(it) }
        })
    }

    private val mappedCsvTable = Table(
        rows = mappedCsvRows,
        columns = mappedCsvColumns,
        stateFactory = { ReadOnlyState(it) },
        changeListener = TableChangeListener.readOnly()
    )

    private val indexColumn = GridTable.Column<ColumnMapping>(weight = 0.0, cellFactory = {
        TableCell(
            node = Label("${it.index}")
        )
    })

    private val nameColumn = GridTable.Column<ColumnMapping>(weight = 1.0, cellFactory = {
        TableCell(
            node = ValidatingTextField(
                converter = Converters.validatingConverter(String::class),
                default = it.name
            )
        )
    })

    private val typeColumn = GridTable.Column<ColumnMapping>(weight = 0.0, cellFactory = {
        TableCell(
            node = Labels.label(it.converter.name)
        )
    })

    private val formatColumn = GridTable.Column<ColumnMapping>(weight = 0.0, cellFactory = {
        TableCell(
            node = ValidatingTextField(
                converter = Converters.validatingConverter(String::class),
                default = "TODO"
            )
        )
    })

    private val actionColumn = GridTable.Column<ColumnMapping>(weight = 0.0, cellFactory = {
        TableCell(
            node = Buttons.delete(Labels.with(CsvColumnConfig::class)) {
                columnMappings.value = columnMappings.value.filter { line -> it.id != line.id }
            }
        )
    })

    private val columnMappingTable = GridTable(
        model = columnMappings,
        indexOf = ColumnMapping::id,
        headerFactory = { values, columns ->
            mapOf(
                Labels.label("Index") to GridTable.Span(indexColumn),
                Labels.label("Name") to GridTable.Span(nameColumn),
                Labels.label("Type") to GridTable.Span(typeColumn),
                Labels.label("Format") to GridTable.Span(formatColumn),

            )
        },
        footerFactory = { values, columns ->
            val selectColumn = ValidatingChoiceBox(
                values = current.csvColumnNames.mapIndexed { index, s -> index to s },
                default = null,
                initialConverter = { if (it!=null) "${it.first}: ${Strings.abbreviate(it.second, 20)}" else "" },
                validate = { null }
            )
            val csvCellConverters = csvCellConverters()
            val newColumnMapping = Buttons.add(Labels.with(CsvColumnConfig::class)) {
                columnMappings.value += ColumnMapping(
                    index = selectColumn.value.first,
                    name = selectColumn.value.second,
                    converter = csvCellConverters.value
                )
            }
            newColumnMapping.disableProperty().bind(ValidatingField.invalidInputs(selectColumn,csvCellConverters))
            
            mapOf(
                selectColumn to GridTable.Span(nameColumn),
                csvCellConverters to GridTable.Span(typeColumn),
                newColumnMapping to GridTable.Span(actionColumn)
            )
        },
        columns = listOf(
            indexColumn,
            nameColumn,
            typeColumn,
            formatColumn,
            actionColumn
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

        row++
        add(mappedCsvTable, Pos(0, row, columnSpan = allColumns))
    }

    override fun enter() {
        
    }

    override fun isValidProperty(): ObservableValue<Boolean> {
        return isValid
    }

    override fun result(): ImportCsvState {
        return current
    }

    private fun csvCellConverters(): ValidatingChoiceBox<CsvCellConverter<*>> {
        return ValidatingChoiceBox(
            values = CsvCellConverters.all,
            default = null,
            initialConverter = { it?.name ?: "" },
            validate = { null }
        )
    }
}