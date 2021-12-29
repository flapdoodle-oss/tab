package de.flapdoodle.fx.layout.panning

import de.flapdoodle.fx.layout.Nodes
import de.flapdoodle.fx.layout.helper.Cross
import javafx.scene.Scene
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Border
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.Stage
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import tornadofx.add


@ExtendWith(ApplicationExtension::class)
internal class VirtualPaneIT {
    @Start
    private fun createElement(stage: Stage) {
        val testee = ScrollPane().apply {
            id="testee"
            isPannable=true
            hbarPolicy=ScrollPane.ScrollBarPolicy.AS_NEEDED
            vbarPolicy=ScrollPane.ScrollBarPolicy.AS_NEEDED
            content=Nodes.region(600.0,600.0, Color.BLUE, Color.RED)
        }

//        val wrapper = Pane().apply {
//            add(testee)
//            id = "wrapper"
//        }

        stage.setScene(Scene(testee, 300.0, 300.0))
        stage.show()
    }

    @Test
    fun shouldMatchInnerDimensions(robot: FxRobot) {
//        Thread.sleep(100000)
        
        Assertions.assertThat(
            robot.lookup("#testee")
                .queryAs(ScrollPane::class.java)
        )
            .extracting(ScrollPane::getWidth,ScrollPane::getHeight)
            .containsExactly(100.0,200.0)

    }

}