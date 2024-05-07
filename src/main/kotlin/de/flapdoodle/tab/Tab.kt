package de.flapdoodle.tab

import de.flapdoodle.tab.model.Tab2Model
import de.flapdoodle.tab.ui.ModelSolverWrapper
import de.flapdoodle.tab.ui.IO
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
    private var lastModel: Tab2Model = Tab2Model()
    private var undoModels = emptyList<Tab2Model>()

    override fun start(stage: Stage) {
        val modelWrapper = ModelSolverWrapper(Tab2Model())
        modelWrapper.model().addListener { _, _, newValue ->
            undoModels = emptyList<Tab2Model>() + lastModel + undoModels
            lastModel = newValue
            if (undoModels.size > 10) {
                undoModels = undoModels.subList(0, 10)
            }
        }

        val root = BorderPane().apply {
            top = MenuBar().also { menuBar ->
                menuBar.menus.add(Menu("Files").also { files ->
                    files.items.add(MenuItem("New").also { item ->
                        item.onAction = EventHandler {
                            modelWrapper.changeModel { Tab2Model() }
                        }
                    })
                    files.items.add(MenuItem("Undo").also { item ->
                        item.accelerator = KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN)
                        item.onAction = EventHandler {
                            var lastUndoList = undoModels
                            if (lastUndoList.isNotEmpty()) {
                                val undo = lastUndoList[0]
                                lastUndoList = lastUndoList.subList(1, lastUndoList.size)
                                modelWrapper.changeModel { undo }

                                lastModel = undo
                                undoModels = lastUndoList
                            }
                        }
                    })
                    files.items.add(MenuItem("Save").also { item ->
                        item.onAction = EventHandler {
                            IO.save(modelWrapper.model().value, stage)
                        }
                    })
                    files.items.add(MenuItem("Load").also { item ->
                        item.onAction = EventHandler {
                            val loaded = IO.load(stage)
                            if (loaded!=null) {
                                modelWrapper.changeModel { loaded }
                            }
                        }
                    })
                    files.items.add(SeparatorMenuItem())
                    files.items.add(MenuItem("Quit").also { quit ->
                        quit.onAction = EventHandler {
                            println("clicked...")
                            Platform.exit()
                            System.exit(0);
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