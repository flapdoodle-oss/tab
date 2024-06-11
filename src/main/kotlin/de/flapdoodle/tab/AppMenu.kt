package de.flapdoodle.tab

import de.flapdoodle.tab.model.Tab2Model
import de.flapdoodle.tab.ui.IO
import de.flapdoodle.tab.ui.ModelSolverWrapper
import de.flapdoodle.tab.ui.resources.Labels
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination

class AppMenu(
    val modelWrapper: ModelSolverWrapper,
) : MenuBar() {
    private val context = Labels.with(AppMenu::class)

    init {
        menus.add(menu("Files").also { files ->
            files.items.add(menuItem("New").also { item ->
                item.onAction = EventHandler {
                    modelWrapper.changeModel { Tab2Model() }
                }
            })
            files.items.add(menuItem("Save").also { item ->
                item.onAction = EventHandler {
                    val saved = IO.save(modelWrapper.model().value, scene.window)
                    if (saved!=null) {
                        modelWrapper.replaceModel { saved }
                    }
                }
            })
            files.items.add(menuItem("Load").also { item ->
                item.onAction = EventHandler {
                    val loaded = IO.load(scene.window)
                    if (loaded!=null) {
                        modelWrapper.changeModel { loaded }
                    }
                }
            })
            files.items.add(SeparatorMenuItem())
            files.items.add(menuItem("Quit").also { quit ->
                quit.onAction = EventHandler {
                    println("clicked...")
                    Platform.exit()
                    System.exit(0);
                }
            })
        })
        menus.add(menu("Edit").also { edit ->
            edit.items.add(menuItem("Undo").also { item ->
                item.accelerator = KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN)
                item.onAction = EventHandler {
                    modelWrapper.undo()
                }
            })
            edit.items.add(menuItem("Redo").also { item ->
                item.accelerator = KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN)
                item.onAction = EventHandler {
                    modelWrapper.redo()
                }
            })
        })
    }

    private fun menu(key: String, fallback: String): Menu {
        return Menu(context.text(key, fallback))
    }
    private fun menu(fallback: String): Menu {
        return Menu(context.text(fallback.lowercase(), fallback))
    }

    private fun menuItem(key: String, fallback: String): MenuItem {
        return MenuItem(context.text(key, fallback))
    }

    private fun menuItem(fallback: String): MenuItem {
        return MenuItem(context.text(fallback.lowercase(), fallback))
    }
}