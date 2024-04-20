package de.flapdoodle.tab.app.ui.io

import de.flapdoodle.fx.extensions.fire
import de.flapdoodle.tab.app.io.Tab2ModelIO
import de.flapdoodle.tab.app.model.Tab2Model
import de.flapdoodle.tab.graph.nodes.renderer.events.UIEvent
import de.flapdoodle.tab.persist.TabModelIO
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.stage.FileChooser
import javafx.stage.Window
import java.nio.file.Files
import java.nio.file.StandardOpenOption

object IO {
    fun save(model: Tab2Model, window: Window) {
        val fileChooser = fileChooser()
        fileChooser.title = "Open File"
        fileChooser.initialFileName = "newFile.tab2"
        val file = fileChooser.showSaveDialog(window)
        println("write to $file")
        if (file!=null) {
            val json = Tab2ModelIO.asJson(model)
            Files.write(file.toPath(),json.toByteArray(Charsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)
        }
    }

    fun load(window: Window): Tab2Model? {
        val fileChooser = fileChooser()
        fileChooser.title = "Open File"
        val file = fileChooser.showOpenDialog(window)
        println("load $file")
        if (file!=null) {
            val content = Files.readAllBytes(file.toPath())
            //model.value(TabModel())
            try {
                val model = Tab2ModelIO.fromJson(String(content, Charsets.UTF_8))
                return model
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
                FileChooser.ExtensionFilter("Tab2 File", "*.tab2")
            )
        }
    }
}