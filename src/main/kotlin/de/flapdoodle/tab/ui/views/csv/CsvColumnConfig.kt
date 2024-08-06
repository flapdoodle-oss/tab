package de.flapdoodle.tab.ui.views.csv

import de.flapdoodle.kfx.css.bindCss
import de.flapdoodle.kfx.dialogs.DialogContent
import de.flapdoodle.kfx.layout.grid.GridPane
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.tab.model.Node
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import java.nio.file.Path

class CsvColumnConfig(
    val state: ImportCsvState?
) : GridPane(), DialogContent<ImportCsvState> {
    private val isValid = SimpleObjectProperty<Boolean>(false)
    private var current = requireNotNull(state) { "state is null"}

    init {
        bindCss("csv-column-config")
        columnWeights(1.0)
        rowWeights(1.0)

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

    override fun title(): String {
        return "TODO"
    }
}