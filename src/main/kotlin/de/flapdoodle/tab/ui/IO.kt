package de.flapdoodle.tab.ui

import de.flapdoodle.tab.model.Model
import de.flapdoodle.tab.model.changes.Change
import de.flapdoodle.tab.prefs.TabPref
import de.flapdoodle.tab.ui.views.csv.ImportCsvTable
import javafx.stage.FileChooser
import javafx.stage.Window
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardOpenOption

object IO {
    fun save(model: Model, window: Window): Model? {
        val fileChooser = fileChooser()
        fileChooser.title = "Open File"
        fileChooser.initialFileName = model.path?.fileName?.toString() ?: "newFile.tab"
        val initialDirectory = model.path?.parent ?: TabPref.fileDirectory()
        if (initialDirectory!=null) {
            fileChooser.initialDirectory = initialDirectory.toFile()
        }

        val file = fileChooser.showSaveDialog(window)
        println("write to $file")
        return if (file!=null) {
            val path = file.toPath()

            TabPref.storeFileDirectory(path.parent)
            val json = de.flapdoodle.tab.io.ModelIO.asJson(model)
            Files.write(path,json.toByteArray(Charsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)
            model.copy(path = path)
        } else {
            null
        }
    }

    fun load(window: Window): Model? {
        val fileChooser = fileChooser()
        fileChooser.title = "Open File"
        TabPref.fileDirectory()?.let {
            fileChooser.initialDirectory = it.toFile()
        }
        val file = fileChooser.showOpenDialog(window)
        println("load $file")
        if (file!=null) {
            val path = file.toPath()
            TabPref.storeFileDirectory(path.parent)
            val content = Files.readAllBytes(path)
            //model.value(TabModel())
            try {
                val model = de.flapdoodle.tab.io.ModelIO.fromJson(String(content, Charsets.UTF_8))
                return model.copy(path = path)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        return null
    }

    fun importCSV(model: Model, window: Window): Model? {
        val fileChooser = FileChooser()
        fileChooser.title = "Open CSV"
        fileChooser.extensionFilters.addAll(FileChooser.ExtensionFilter("csv","*.csv"))
        TabPref.fileDirectory()?.let {
            fileChooser.initialDirectory = it.toFile()
        }
        val file = fileChooser.showOpenDialog(window)
        if (file != null) {
            val newTable = ImportCsvTable.open(window, file.toPath())
            if (newTable != null) {
                return model.apply(Change.AddNode(newTable))
            }
        }
        return null
    }

    private fun fileChooser(): FileChooser {
        return FileChooser().apply {
            extensionFilters.addAll(
                // TODO i18n
                FileChooser.ExtensionFilter("All Files", "*.*"),
                FileChooser.ExtensionFilter("Tab File", "*.tab")
            )
        }
    }
}