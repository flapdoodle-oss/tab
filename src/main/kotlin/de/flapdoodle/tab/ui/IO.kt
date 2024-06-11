package de.flapdoodle.tab.ui

import de.flapdoodle.tab.model.Tab2Model
import de.flapdoodle.tab.prefs.TabPref
import javafx.stage.FileChooser
import javafx.stage.Window
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

object IO {
    fun save(model: Tab2Model, window: Window): Tab2Model? {
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
            val json = de.flapdoodle.tab.io.Tab2ModelIO.asJson(model)
            Files.write(path,json.toByteArray(Charsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)
            model.copy(path = path)
        } else {
            null
        }
    }

    fun load(window: Window): Tab2Model? {
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
                val model = de.flapdoodle.tab.io.Tab2ModelIO.fromJson(String(content, Charsets.UTF_8))
                return model.copy(path = path)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        return null
    }

    private fun fileChooser(): FileChooser {
        return FileChooser().apply {
            extensionFilters.addAll(
                FileChooser.ExtensionFilter("All Files", "*.*"),
                FileChooser.ExtensionFilter("Tab File", "*.tab")
            )
        }
    }
}