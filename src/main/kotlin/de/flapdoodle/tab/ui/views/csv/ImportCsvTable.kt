package de.flapdoodle.tab.ui.views.csv

import de.flapdoodle.kfx.css.bindCss
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.ui.dialogs.DialogContent
import de.flapdoodle.tab.ui.dialogs.DialogWrapper
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.stage.Window
import java.nio.file.Path

class ImportCsvTable(
    val path: Path
) : DialogContent<Node.Table<out Comparable<*>>>() {
    private val isValid = SimpleObjectProperty<Boolean>(false)

    init {
        bindCss("import-csv")
        columnWeights(1.0)
        rowWeights(1.0)

        add(CsvFormatPane(path), 0, 0)
    }

    override fun isValidProperty(): ObservableValue<Boolean> {
        return isValid
    }

    override fun result(): Node.Table<out Comparable<*>>? {
        return null
    }

    companion object {
        fun open(window: Window, path: Path): Node.Table<out Comparable<*>>? {
            return DialogWrapper.open(window) { ImportCsvTable(path) }
        }
    }

}