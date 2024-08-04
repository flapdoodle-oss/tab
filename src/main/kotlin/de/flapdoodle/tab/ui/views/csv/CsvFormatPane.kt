package de.flapdoodle.tab.ui.views.csv

import de.flapdoodle.kfx.bindings.ObjectBindings
import de.flapdoodle.kfx.controls.bettertable.Column
import de.flapdoodle.kfx.controls.bettertable.ColumnProperty
import de.flapdoodle.kfx.controls.bettertable.Table
import de.flapdoodle.kfx.controls.bettertable.TableChangeListener
import de.flapdoodle.kfx.controls.bettertable.events.ReadOnlyState
import de.flapdoodle.kfx.controls.fields.ValidatingChoiceBox
import de.flapdoodle.kfx.css.cssClassName
import de.flapdoodle.kfx.layout.grid.GridPane
import de.flapdoodle.kfx.layout.grid.Pos
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.io.csv.Format
import de.flapdoodle.tab.io.csv.ImportCSV
import de.flapdoodle.tab.ui.resources.Labels
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.util.StringConverter
import java.io.IOException
import java.io.StringReader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

class CsvFormatPane(val path: Path) : GridPane() {
    private val charsetStringConverter: StringConverter<Charset> = object : StringConverter<Charset>() {
        override fun toString(value: Charset?): String? {
            return value?.displayName()
        }

        override fun fromString(value: String?): Charset? {
            return if (value!=null) Charset.forName(value) else null
        }
    }

    private val charConverter: StringConverter<Char> = object : StringConverter<Char>() {
        override fun toString(value: Char?): String? {
            return value?.toString()
        }

        override fun fromString(value: String?): Char? {
            return value?.first()
        }
    }

    private val encodingLabel = Labels.label(CsvFormatPane::class, "encoding", "Encoding")
    private val encoding = ValidatingChoiceBox<Charset>(
        values = listOf(StandardCharsets.UTF_8, StandardCharsets.ISO_8859_1),
        default = StandardCharsets.UTF_8,
        initialConverter = charsetStringConverter,
        validate = { null }
    )

    private val formatLabel = Labels.label(CsvFormatPane::class, "format", "Format")
    private val separator = ValidatingChoiceBox(
        values = listOf(Format.COMMA, Format.TAB, Format.COLON, Format.SEMICOLON),
        default = Format.COMMA,
        initialConverter = charConverter,
        validate = { null }
    )

    private val csvFileContent = SimpleObjectProperty<String>(null)
    private val csvFileReadError = SimpleStringProperty(null)
    
    private val csvFile = SimpleObjectProperty<List<List<String>>>(emptyList())
    private val csvColumns =  SimpleObjectProperty<List<Column<List<String>, out Any>>>(emptyList())

    private val csvTable = Table(
        rows = csvFile,
        columns = csvColumns,
        stateFactory = { ReadOnlyState(it) },
        changeListener = object : TableChangeListener<List<String>> {
            override fun changeCell(
                row: List<String>,
                change: TableChangeListener.CellChange<List<String>, out Any>
            ): TableChangeListener.ChangedRow<List<String>> {
                TODO("Not yet implemented")
            }

            override fun emptyRow(index: Int): List<String> {
                TODO("Not yet implemented")
            }

            override fun updateRow(
                row: List<String>,
                changed: List<String>,
                errors: List<TableChangeListener.CellError<List<String>, out Any>>
            ) {
                TODO("Not yet implemented")
            }

            override fun removeRow(row: List<String>) {
                TODO("Not yet implemented")
            }

            override fun insertRow(index: Int, row: List<String>): Boolean {
                TODO("Not yet implemented")
            }
        }
    )

    init {
        cssClassName("csv-format")
        columnWeights(0.0, 1.0, 1.0)

        var row=0
        add(encodingLabel, Pos(0, row))
        add(encoding, Pos(1, row))
        row++
        add(Labels.label(csvFileReadError), Pos(0, row, columnSpan = 2))
        row++
        add(formatLabel, Pos(0, row))
        add(separator, Pos(1, row))
        row++

        csvTable.maxWidth = 400.0
        add(csvTable, Pos(0, row, columnSpan = 3))

        readCsvFile(path, encoding.value)
        encoding.valueProperty().addListener { observable, oldValue, newValue ->
            readCsvFile(path, newValue)
        }

        csvFile.bind(ObjectBindings.merge(csvFileContent, separator.valueProperty()) { csv, separatorValue ->
            val read = ImportCSV.read(StringReader(csv), Format(separator = separatorValue))
            read
        })
        csvFile.addListener { observable, oldValue, csv ->
            
        }
        csvColumns.bind(ObjectBindings.map(csvFile) { file ->
            if (file.size > 0) {
                val header = file.get(0)
                header.mapIndexed { index, label -> Column(label, ColumnProperty<List<String>, String>(
                    type = TypeInfo.of(String::class.java),
                    getter = { row -> row.get(index)}
                ), false) }
            } else emptyList<Column<List<String>, out Any>>()
        })
    }

    private fun readCsvFile(path: Path, encoding: Charset) {
        try {
            val csvAsString = Files.readString(path, encoding)
            csvFileContent.value = csvAsString
            csvFileReadError.value = null
        } catch (ex: IOException) {
            csvFileContent.value = null
            csvFileReadError.value =  ex.localizedMessage // I18N.exceptionMessage(ResourceBundles.exceptions(), IOException::class, ex.)
        }
    }
}