package de.flapdoodle.tab.model.modifier

import de.flapdoodle.tab.model.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

class ModifierFactoryTest {
    @Test
    fun addAndRemoveNode() {
        val node = randomNode()

        assertThat(changesFor(Change.AddNode(node)))
            .containsExactly(AddNode(node))
        assertThat(changesFor(Change.RemoveNode(node.id)))
            .containsExactly(RemoveNode(node.id))
    }

    @Test
    fun moveAndResize() {
        val node = randomNode()
        val position = Position(10.0, 20.0)
        val size = Size(3.0, 4.0)

        assertThat(changesFor(Change.Move(node.id, position)))
            .containsExactly(Move(node.id, position))
        assertThat(changesFor(Change.Resize(node.id, position, size)))
            .containsExactly(Resize(node.id, position, size))
    }

    private fun changesFor(change: Change): List<Modifier> {
        return ModifierFactory.changes(emptyList(), change)
    }

    private fun randomNode(): Node {
        val node = Node.Constants(Title(UUID.randomUUID().toString()))
        return node
    }
}