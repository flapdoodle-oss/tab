package de.flapdoodle.tab.app.io.mapper

import de.flapdoodle.tab.app.io.MemorizingMapping
import de.flapdoodle.tab.app.io.file.FileNode
import de.flapdoodle.tab.app.io.file.Tab2File
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.Position
import de.flapdoodle.tab.app.model.Tab2Model
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DefaultModelFileMapperTest {

    @Test
    fun testDelegation() {
        val testNode = Node.Constants(name = "in")
        val testFileNode = FileNode(
            name ="out",
            position = Position(0.0, 0.0),
            id = "id"
        )

        val testee = DefaultModelFileMapper(nodeMapper = StaticTestMapper(testNode, testFileNode))

        val result = testee.toFile(MemorizingMapping().toFileMapping(), Tab2Model(listOf(testNode)))
        assertThat(result.nodes)
            .hasSize(1)
            .containsExactly(testFileNode)

        val readBack = testee.toModel(MemorizingMapping().toModelMapping(), Tab2File(listOf(testFileNode)))
        assertThat(readBack.nodes)
            .hasSize(1)
            .containsExactly(testNode)
    }
}