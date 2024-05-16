package de.flapdoodle.tab

import de.flapdoodle.tab.model.Tab2Model
import de.flapdoodle.tab.ui.ModelSolverWrapper
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage

class Tab : Application() {
    override fun start(stage: Stage) {
        val modelWrapper = ModelSolverWrapper(Tab2Model())

        stage.scene= Scene(Main(modelWrapper), 800.0, 600.0)
        stage.show()
    }

    override fun stop() {
        // wird nur aufgerufen, wenn jemand auf close window clickt..
        println("stop called.. ")
        super.stop()
    }
}