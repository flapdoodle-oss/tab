package de.flapdoodle.tab

import de.flapdoodle.tab.model.Tab2Model
import de.flapdoodle.tab.ui.ModelSolverWrapper
import de.flapdoodle.tab.ui.IO
import de.flapdoodle.tab.ui.resources.Labels
import javafx.application.Application
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.layout.BorderPane
import javafx.stage.Stage

class Tab : Application() {
    private val context = Labels.with(Tab::class)

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

    override fun start(stage: Stage) {
        val modelWrapper = ModelSolverWrapper(Tab2Model())


        val root = BorderPane().apply {
            top = MenuBar().also { menuBar ->
                menuBar.menus.add(menu("Files").also { files ->
                    files.items.add(menuItem("New").also { item ->
                        item.onAction = EventHandler {
                            modelWrapper.changeModel { Tab2Model() }
                        }
                    })
                    files.items.add(menuItem("Save").also { item ->
                        item.onAction = EventHandler {
                            IO.save(modelWrapper.model().value, stage)
                        }
                    })
                    files.items.add(menuItem("Load").also { item ->
                        item.onAction = EventHandler {
                            val loaded = IO.load(stage)
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
                menuBar.menus.add(menu("Edit").also { edit ->
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
            center = Main(modelWrapper)
        }

        stage.scene= Scene(root, 800.0, 600.0)
        stage.show()
    }

    override fun stop() {
        // wird nur aufgerufen, wenn jemand auf close window clickt..
        println("stop called.. ")
        super.stop()
    }
}