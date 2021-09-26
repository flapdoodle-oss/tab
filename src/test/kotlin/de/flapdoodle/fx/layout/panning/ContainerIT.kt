package de.flapdoodle.fx.layout.panning

import de.flapdoodle.fx.layout.Nodes
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.input.MouseButton
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.Stage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import tornadofx.add

@ExtendWith(ApplicationExtension::class)
internal class ContainerIT {

    private var clicked = false

    @Start
    private fun createElement(stage: Stage) {
        val wrapped = Nodes.region(100.0,200.0, Color.BLUE).also {
            it.id="wrapped"
            
            it.onMouseClicked = EventHandler {
                println("clicked...")
                clicked = true
            }
//            it.onMouseEntered = EventHandler {
//                println("... enter ...")
//            }
//            it.addEventFilter(MouseEvent.ANY, { event ->
//                println("-> $event")
//            })
        }

//        val button = Button().apply {
//            addEventFilter(MouseEvent.ANY, { event ->
//                println("-> $event")
//            })
//
//            onLeftClick { println("left") }
//        }
//
        val testee = Container().apply {
            id="testee"
            setContent(wrapped)
        }

        val wrapper = Pane().apply {
            add(testee)
            id = "wrapper"
        }

        stage.setScene(Scene(wrapper, 300.0, 300.0))
        stage.show()
    }

    @Test
    fun shouldMatchInnerDimensions(robot: FxRobot) {
        assertThat(robot.lookup("#testee")
            .queryAs(Container::class.java))
            .extracting(Container::getWidth,Container::getHeight)
            .containsExactly(100.0,200.0)
    }

    @Test
    fun mouseEventReachesContent(robot: FxRobot) {
//        FX.run {
//            Thread.sleep(10000)
//        }
//
        robot.clickOn(robot.point("#testee"), MouseButton.PRIMARY)
        assertThat(clicked).isTrue()
    }
}