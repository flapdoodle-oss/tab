package de.flapdoodle.tab.io.mapper

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.io.MemorizingMapping
import de.flapdoodle.tab.io.file.FileColumns
import de.flapdoodle.tab.io.file.FileNode
import de.flapdoodle.tab.io.file.FileSingleValues
import de.flapdoodle.tab.model.Position
import de.flapdoodle.tab.model.Title
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DefaultNodeMapperTest {

    @Test
    fun testDelegationForConstants() {
        val constants = de.flapdoodle.tab.model.Node.Constants(name = Title("in"))
        val fileNodeConstants = FileNode(
            name = "out",
            short = "short",
            description = "description",
            position = Position(0.0, 0.0),
            id = "id",
            constants = FileNode.Constants(
                values = FileSingleValues(
                    values = emptyList()
                )
            )
        )
        val testee = DefaultNodeMapper(
            constantsMapper = StaticTestMapper(constants, fileNodeConstants)
        )

        val result = testee.toFile(MemorizingMapping().toFileMapping(), constants)
        assertThat(result).isEqualTo(fileNodeConstants)

        val readBack = testee.toModel(MemorizingMapping().toModelMapping(), fileNodeConstants)
        assertThat(readBack).isEqualTo(constants)
    }

    @Test
    fun testDelegationForTable() {
        val table = de.flapdoodle.tab.model.Node.Table(name = Title("inTab"), TypeInfo.of(Int::class.javaObjectType))
        val fileNodeTable = FileNode(
            name = "outTab",
            short = "short",
            description = "description",
            position = Position(0.0, 0.0),
            id = "idTab",
            table = FileNode.Table(
                indexType = "Int",
                columns = FileColumns(
                    values = emptyList()
                )
            )
        )

        val testee = DefaultNodeMapper(
            tableMapper = StaticTestMapper(table, fileNodeTable)
        )

        val result = testee.toFile(MemorizingMapping().toFileMapping(), table)
        assertThat(result).isEqualTo(fileNodeTable)

        val readBack = testee.toModel(MemorizingMapping().toModelMapping(), fileNodeTable)
        assertThat(readBack).isEqualTo(table)
    }
}