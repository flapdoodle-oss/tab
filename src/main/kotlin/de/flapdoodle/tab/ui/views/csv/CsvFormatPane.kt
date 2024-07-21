package de.flapdoodle.tab.ui.views.csv

import de.flapdoodle.kfx.controls.fields.ValidatingChoiceBox
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.tab.ui.resources.Labels
import javafx.util.StringConverter
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Path

class CsvFormatPane(val path: Path) : WeightGridPane() {
    private val charsetStringConverter: StringConverter<Charset> = object : StringConverter<Charset>() {
        override fun toString(value: Charset): String {
            return value.displayName()
        }

        override fun fromString(value: String): Charset {
            return Charset.forName(value)
        }
    }

    private val encodingLabel = Labels.label(CsvFormatPane::class, "encoding", "Encoding")
    private val encoding = ValidatingChoiceBox<Charset>(
        values = listOf(StandardCharsets.UTF_8, StandardCharsets.ISO_8859_1),
        default = StandardCharsets.UTF_8,
        initialConverter = charsetStringConverter,
        validate = { null }
    )


    init {
        columnWeights(0.0, 1.0)
        
        add(encodingLabel, 0, 0)
        add(encoding, 1, 0)
    }
}