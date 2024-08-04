package de.flapdoodle.tab.ui.views.csv

import de.flapdoodle.kfx.css.bindCss
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.ui.dialogs.WizardContent
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import java.nio.file.Path

class CsvColumnConfig(
    val state: ImportCsvState
) : WizardContent<ImportCsvState>() {
    private val isValid = SimpleObjectProperty<Boolean>(false)

    init {
        bindCss("csv-column-config")
        columnWeights(1.0)
        rowWeights(1.0)

//        add(CsvFormatPane(state.path), 0, 0)
    }

    override fun isValidProperty(): ObservableValue<Boolean> {
        return isValid
    }

    override fun result(): ImportCsvState {
        return state
    }
}