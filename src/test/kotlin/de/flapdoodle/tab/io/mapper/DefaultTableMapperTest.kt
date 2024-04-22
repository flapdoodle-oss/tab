package de.flapdoodle.tab.io.mapper

import de.flapdoodle.tab.io.MemorizingMapping
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.Position
import de.flapdoodle.tab.model.data.Column
import de.flapdoodle.tab.model.data.ColumnId
import de.flapdoodle.tab.model.data.Columns
import javafx.scene.paint.Color
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DefaultTableMapperTest {
    @Test
    fun mapTable() {
        val memorizingMapping = MemorizingMapping()

        val src = de.flapdoodle.tab.model.Node.Table(
            name = "name",
            indexType = Int::class,
            position = Position(10.0, 20.0),
            columns = Columns(listOf(
                Column(
                    name = "column",
                    indexType = Int::class,
                    valueType = String::class,
                    id = ColumnId(),
                    color = Color.YELLOW,
                    values = mapOf(
                        1 to "One",
                        2 to "Two"
                    )
                )
            ))
        )

        val testee = DefaultTableMapper()

        val mapped = testee.toFile(memorizingMapping.toFileMapping(), src)
        val readBack = testee.toModel(memorizingMapping.toModelMapping(), mapped)

        assertThat(readBack).isEqualTo(src)
    }

}