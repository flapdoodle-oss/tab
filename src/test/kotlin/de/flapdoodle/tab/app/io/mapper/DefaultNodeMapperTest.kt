package de.flapdoodle.tab.app.io.mapper

import de.flapdoodle.tab.app.io.MemorizingMapping
import de.flapdoodle.tab.app.io.file.FileNode
import de.flapdoodle.tab.app.io.file.FileSingleValues
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.Position
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DefaultNodeMapperTest {

    @Test
    fun testDelegation() {
        val testNode = Node.Constants(name = "in")
        val testFileNode = FileNode(
            name ="out",
            position = Position(0.0, 0.0),
            id = "id",
            constants = FileNode.Constants(
                values = FileSingleValues(
                    values = emptyList()
                )
            )
        )

        val testee = DefaultNodeMapper(constantsMapper = StaticTestMapper(testNode, testFileNode))

        val result = testee.toFile(MemorizingMapping().toFileMapping(), testNode)
        assertThat(result).isEqualTo(testFileNode)

        val readBack = testee.toModel(MemorizingMapping().toModelMapping(), testFileNode)
        assertThat(readBack).isEqualTo(testNode)

    }
}