package de.flapdoodle.tab.io.mapper

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.io.MemorizingMapping
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.Position
import de.flapdoodle.tab.model.Title
import de.flapdoodle.tab.model.data.SingleValue
import de.flapdoodle.tab.model.data.SingleValues
import javafx.scene.paint.Color
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DefaultConstantsMapperTest {

    @Test
    fun mapConstants() {
        val memorizingMapping = MemorizingMapping()

        val src = de.flapdoodle.tab.model.Node.Constants(
            name = Title("name"),
            position = Position(10.0, 20.0),
            values = SingleValues(
                values = listOf(
                    SingleValue(
                    name = Name("value"),
                    valueType = TypeInfo.of(Int::class.javaObjectType),
                    value = 1231,
                    color = Color.RED
                )
                )
            )
        )

        val testee = DefaultConstantsMapper()

        val mapped = testee.toFile(memorizingMapping.toFileMapping(), src)
        val readBack = testee.toModel(memorizingMapping.toModelMapping(), mapped)

        assertThat(readBack).isEqualTo(src)
    }
}