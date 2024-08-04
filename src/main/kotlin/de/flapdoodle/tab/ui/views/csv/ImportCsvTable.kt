package de.flapdoodle.tab.ui.views.csv

import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.ui.dialogs.WizardWrapper
import javafx.stage.Window
import java.nio.file.Path

object ImportCsvTable {
    fun open(window: Window, path: Path): Node.Table<out Comparable<*>>? {
        val result = WizardWrapper.open(
            window = window,
            inital = ImportCsvState(path),
            ::CsvSourceConfig,
            ::CsvColumnConfig
        )
        return result?.table
    }
}