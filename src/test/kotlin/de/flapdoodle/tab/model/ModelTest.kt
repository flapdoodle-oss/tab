package de.flapdoodle.tab.model

import de.flapdoodle.tab.model.changes.Change
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

class ModelTest {
    @Test
    fun addAndRemoveNode() {
        val a = randomNode()

        val withNode = Model()
            .apply(Change.AddNode(randomNode()))
            .apply(Change.AddNode(a))
            .apply(Change.AddNode(randomNode()))

        assertThat(withNode.nodes())
            .hasSize(3)
            .contains(a)

        val removed = withNode.apply(Change.RemoveNode(a.id))

        assertThat(removed.nodes())
            .hasSize(2)
            .doesNotContain(a)
    }

    private fun randomNode(): Node {
        val node = Node.Constants(Title(UUID.randomUUID().toString()))
        return node
    }
}