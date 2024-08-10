package de.flapdoodle.tab.ui.views.csv

import de.flapdoodle.kfx.dialogs.Wizard
import de.flapdoodle.tab.model.Node
import javafx.stage.Window
import java.nio.file.Path

object ImportCsvTable {
    fun open(window: Window, path: Path): Node.Table<out Comparable<*>>? {
        val result = Wizard.open(window, ImportCsvState(path),
            ::CsvFormatConfig,
            ::CsvColumnConfig)
        
        return result?.table
    }
//    fun open(window: Window, path: Path): Node.Table<out Comparable<*>>? {
//        val result = WizardWrapper.open(
//            window = window,
//            inital = ImportCsvState(path),
//            ::CsvSourceConfig,
//            ::CsvColumnConfig
//        )
//        return result?.table
//    }
}