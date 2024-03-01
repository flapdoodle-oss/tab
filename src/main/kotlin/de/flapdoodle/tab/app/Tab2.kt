package de.flapdoodle.tab.app

import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.layout.BorderPane
import javafx.stage.Stage

class Tab2 : Application() {
    override fun start(stage: Stage) {
        val root = BorderPane().apply {
            top = MenuBar().also { menuBar ->
                menuBar.menus.add(Menu("Files").also { files ->
                    files.items.add(MenuItem("Quit").also { quit ->
                        quit.onAction = EventHandler {
                            println("clicked...")
                        }
                    })
                })
            }
            center = Main2()
        }

        stage.scene= Scene(root, 800.0, 600.0)
        stage.show()
    }

    override fun stop() {

        super.stop()
    }
}