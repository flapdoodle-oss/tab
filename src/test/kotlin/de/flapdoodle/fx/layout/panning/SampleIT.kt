package de.flapdoodle.fx.layout.panning

import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.assertions.api.Assertions
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start


@ExtendWith(ApplicationExtension::class)
internal class SampleIT {

    companion object {
        var oldValue: String? = null

        @BeforeAll
        @JvmStatic
        fun woohoo() {
            println("before all")
//            oldValue = System.setProperty("java.awt.headless","true")
        }

        @AfterAll
        @JvmStatic
        fun woohooEnd() {
            oldValue.let {
//                if (it!=null) System.setProperty("java.awt.headless", it)
            }
            println("after all")
        }
    }

    @Test
    fun testMee() {
        
    }

    private var button: Button? = null

    /**
     * Will be called with `@Before` semantics, i. e. before each test method.
     *
     * @param stage - Will be injected by the test runner.
     */
    @Start
    private fun start(stage: Stage) {
        button = Button("click me!")
        button!!.setId("myButton")
        button!!.setOnAction { actionEvent -> button!!.setText("clicked!") }
        stage.setScene(Scene(StackPane(button), 100.0, 100.0))
        stage.show()
    }

    /**
     * @param robot - Will be injected by the test runner.
     */
    @Test
    fun should_contain_button_with_text(robot: FxRobot) {
        Assertions.assertThat(button).hasText("click me!")
        // or (lookup by css id):
        Assertions.assertThat(robot.lookup("#myButton").queryAs(Button::class.java)).hasText("click me!")
        // or (lookup by css class):
        Assertions.assertThat(robot.lookup(".button").queryAs(Button::class.java)).hasText("click me!")
        // or (query specific type):
        Assertions.assertThat(robot.lookup(".button").queryButton()).hasText("click me!")
    }

}