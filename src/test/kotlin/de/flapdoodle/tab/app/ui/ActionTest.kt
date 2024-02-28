package de.flapdoodle.tab.app.ui

import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.Tab2Model
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ActionTest {

    @Test
    fun noChange() {
        assertThat(Action.syncActions(emptyModel(), emptyModel()))
            .isEmpty()
    }

    @Test
    fun addNode() {
        val node = Node.Constants("x")
        assertThat(Action.syncActions(emptyModel(), emptyModel().addNode(node)))
            .isNotEmpty()
    }

    private fun emptyModel() = Tab2Model()
}