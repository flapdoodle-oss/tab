package de.flapdoodle.tab

import de.flapdoodle.tab.model.Tab2Model
import de.flapdoodle.tab.prefs.TabPref
import de.flapdoodle.tab.ui.ModelSolverWrapper
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.stage.Stage

class Tab : Application() {
    override fun start(stage: Stage) {
        val modelWrapper = ModelSolverWrapper(Tab2Model())

        val windowSize = TabPref.windowPosition()

        stage.scene = Scene(Main(modelWrapper))
        if (windowSize != null) {
            stage.x = windowSize.x
            stage.y = windowSize.y
            stage.width = windowSize.width
            stage.height = windowSize.height
        }
        stage.onCloseRequest = EventHandler {
            TabPref.storeWindowPosition(TabPref.WindowPosition(stage.x, stage.y, stage.width, stage.height))
        }
        stage.show()
    }

    override fun stop() {
        // wird nur aufgerufen, wenn jemand auf close window clickt..
        println("stop called.. ")
        super.stop()
    }
}