package de.flapdoodle.tab.model.modifier

import de.flapdoodle.tab.model.Change
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.Title
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

class ModifierFactoryTest {
    @Test
    fun addNode() {
        val node = randomNode()

        val changes = ModifierFactory.changes(emptyList(), Change.AddNode(node))

        assertThat(changes)
            .containsExactly(AddNode(node))
    }

    @Test
    fun removeNode() {
        val node = randomNode()

        val changes = ModifierFactory.changes(listOf(node), Change.RemoveNode(node.id))

        assertThat(changes)
            .containsExactly(RemoveNode(node.id))
    }

    private fun randomNode(): Node {
        val node = Node.Constants(Title(UUID.randomUUID().toString()))
        return node
    }
}