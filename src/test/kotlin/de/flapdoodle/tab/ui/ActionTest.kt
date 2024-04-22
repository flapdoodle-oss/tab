package de.flapdoodle.tab.ui

import de.flapdoodle.tab.model.Tab2Model
import de.flapdoodle.tab.ui.Action
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
        val node = de.flapdoodle.tab.model.Node.Constants("x")
        assertThat(Action.syncActions(emptyModel(), emptyModel().addNode(node)))
            .isNotEmpty()
    }

    private fun emptyModel() = Tab2Model()
}