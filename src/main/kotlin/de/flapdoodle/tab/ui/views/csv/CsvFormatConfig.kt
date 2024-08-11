package de.flapdoodle.tab.ui.views.csv

import de.flapdoodle.kfx.bindings.ObjectBindings
import de.flapdoodle.kfx.controls.bettertable.Column
import de.flapdoodle.kfx.controls.bettertable.ColumnProperty
import de.flapdoodle.kfx.controls.bettertable.Table
import de.flapdoodle.kfx.controls.bettertable.TableChangeListener
import de.flapdoodle.kfx.controls.bettertable.events.ReadOnlyState
import de.flapdoodle.kfx.controls.fields.ValidatingChoiceBox
import de.flapdoodle.kfx.css.bindCss
import de.flapdoodle.kfx.layout.grid.Pos
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.io.csv.CommonCSV
import de.flapdoodle.tab.io.csv.Format
import de.flapdoodle.tab.io.csv.ImportCSV
import de.flapdoodle.tab.ui.resources.Labels
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.geometry.HPos
import javafx.scene.control.TextArea
import javafx.util.StringConverter
import javafx.util.converter.IntegerStringConverter
import java.io.IOException
import java.io.StringReader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

class CsvFormatConfig(
    val config: ImportCsvState?
) : AbstractCsvDialogStep<ImportCsvState>() {

    private var current = requireNotNull(config) { "config is null" }
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

    private val encodingLabel = Labels.label(CsvFormatConfig::class, "encoding", "Encoding")
    private val encoding = ValidatingChoiceBox(
        values = listOf(StandardCharsets.UTF_8, StandardCharsets.ISO_8859_1),
        default = StandardCharsets.UTF_8,
        initialConverter = charsetStringConverter,
        validate = { null }
    )

    private val formatLabel = Labels.label(CsvFormatConfig::class, "format", "Format")
    private val separator = ValidatingChoiceBox(
        values = listOf(Format.COMMA, Format.TAB, Format.COLON, Format.SEMICOLON),
        default = Format.COMMA,
        initialConverter = charConverter,
        validate = { null }
    )

    private val quoteLabel = Labels.label(CsvFormatConfig::class, "quote", "Quote")
    private val quote = ValidatingChoiceBox(
        values = listOf(Format.SINGLE_QUOTE, Format.DOUBLE_QUOTE),
        default = Format.DOUBLE_QUOTE,
        initialConverter = charConverter,
        validate = { null }
    )

    private val headerRowsLabel = Labels.label(CsvFormatConfig::class, "header_rows", "Header Rows")
    private val headerRows = ValidatingChoiceBox(
        values = listOf(1, 2, 3, 4),
        default = 1,
        initialConverter = IntegerStringConverter(),
        validate = { null }
    )

    private val csvFileContent = SimpleObjectProperty<String>(null)
    private val csvFileReadError = SimpleStringProperty(null)
    
    private val csvFile = SimpleObjectProperty<List<List<String>>>(emptyList())
    private val csvColumnNames =  SimpleObjectProperty<List<String>>(emptyList())
    private val csvColumns =  SimpleObjectProperty<List<Column<List<String>, out Any>>>(emptyList())
    private val csvRows = SimpleObjectProperty<List<List<String>>>(emptyList())

    private val isValid = SimpleObjectProperty<Boolean>(false)

    private val csvTable = Table(
        rows = csvRows,
        columns = csvColumns,
        stateFactory = { ReadOnlyState(it) },
        changeListener = TableChangeListener.readOnly()
    )

    init {
        bindCss("csv-format")
        columnWeights(0.0, 1.0)

        var row=0
        val allColumns = 2

        add(encodingLabel, Pos(0, row))
        add(encoding, Pos(1, row), HPos.LEFT)

        row++
        add(Labels.label(csvFileReadError), Pos(0, row, columnSpan = allColumns))

        row++
        add(TextArea("foo").apply {
            textProperty().bind(csvFileContent)
        }, Pos(0, row, columnSpan = allColumns))

        row++
        add(formatLabel, Pos(0, row))
        add(separator, Pos(1, row), HPos.LEFT)

        row++
        add(quoteLabel, Pos(0, row))
        add(quote, Pos(1, row), HPos.LEFT)

        row++
        add(headerRowsLabel, Pos(0, row))
        add(headerRows, Pos(1, row), HPos.LEFT)

        row++
        csvTable.prefWidth = 800.0
        csvTable.prefHeight = 200.0
        add(csvTable, Pos(0, row, columnSpan = allColumns))

        readCsvFile(current.path, encoding.value)
        encoding.valueProperty().addListener { observable, oldValue, newValue ->
            readCsvFile(current.path, newValue)
        }

        csvFile.bind(ObjectBindings.merge(csvFileContent, separator.valueProperty(), quote.valueProperty()) { csv, separatorValue, quoteValue ->
            if (csv!=null) {
                val read = ImportCSV.read(StringReader(csv), Format(separator = separatorValue, quote = quoteValue))
                read
            } else emptyList()
        })
        csvFile.addListener { observable, oldValue, csv ->
            
        }
        csvColumnNames.bind(ObjectBindings.merge(csvFile, headerRows.valueProperty()) { file, rowSize ->
            if (file.size >= rowSize) {
                val headers = file.subList(0, rowSize)
                val columns = headers.maxOf { it.size }

                (0..<columns).map { column ->
                    val label = headers.map { if (it.size > column) it[column] else "" }.joinToString(" + ")
                    label
                }
            } else emptyList()
        })

        csvColumns.bind(ObjectBindings.map(csvColumnNames) { labels ->
            labels.mapIndexed { column, label ->
                Column(label, ColumnProperty<List<String>, String>(
                    type = TypeInfo.of(String::class.java),
                    getter = { row -> if (row.size > column) row[column] else null }
                ), false)
            }
        })

        csvRows.bind(ObjectBindings.merge(csvFile, headerRows.valueProperty()) { file, rowSize ->
            if (file.size >= rowSize) {
                file.subList(rowSize, file.size)
            } else emptyList()
        })

        isValid.bind(ObjectBindings.merge(csvRows, csvColumns) { row, columns ->
            row.isNotEmpty() && columns.isNotEmpty()
        })
    }

    private fun readCsvFile(path: Path, encoding: Charset) {
        try {
            val csvAsString = Files.readString(path, encoding)
            csvFileContent.value = csvAsString
            csvFileReadError.value = null

            val guessedFormat = CommonCSV.guessFormat(StringReader(csvAsString))
            separator.set(guessedFormat.separator)
            quote.set(guessedFormat.quote)
        } catch (ex: IOException) {
            csvFileContent.value = null
            csvFileReadError.value =  ex.localizedMessage // I18N.exceptionMessage(ResourceBundles.exceptions(), IOException::class, ex.)
        }
    }

    override fun enter() {

    }

    override fun isValidProperty(): ObservableValue<Boolean> {
        return isValid
    }

    override fun result(): ImportCsvState {
        return current.copy(csvColumnNames = csvColumnNames.value, csvRows = csvRows.value)
    }
}