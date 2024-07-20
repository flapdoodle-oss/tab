package de.flapdoodle.tab.ui.views.csv

import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.ui.dialogs.DialogContent
import de.flapdoodle.tab.ui.dialogs.DialogWrapper
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import java.io.File

class ImportCsvTable(
    val file: File
) : DialogContent<Node.Table<out Comparable<*>>>() {
    private val isValid = SimpleObjectProperty<Boolean>(false)

    init {
        bindCss("import-csv")
    }

    override fun isValidProperty(): ObservableValue<Boolean> {
        return isValid
    }

    override fun result(): Node.Table<out Comparable<*>>? {
        return null
    }

    companion object {
        fun open(file: File): Node.Table<out Comparable<*>>? {
            return DialogWrapper.open { ImportCsvTable(file) }
        }
    }

}