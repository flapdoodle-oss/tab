package de.flapdoodle.tab.app.io.mapper

import de.flapdoodle.tab.app.io.MemorizingMapping
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.Position
import de.flapdoodle.tab.app.model.data.SingleValue
import de.flapdoodle.tab.app.model.data.SingleValues
import javafx.scene.paint.Color
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ConstantsMapperTest {

    @Test
    fun mapConstants() {
        val memorizingMapping = MemorizingMapping()

        val src = Node.Constants(
            name = "name",
            position = Position(10.0, 20.0),
            values = SingleValues(
                values = listOf(
                    SingleValue(
                    name = "value",
                    valueType = Int::class,
                    value = 1231,
                    color = Color.RED
                )
                )
            )
        )

        val mapped = ConstantsMapper().toFile(memorizingMapping.toFileMapping(), src)
        val readBack = ConstantsMapper().toModel(memorizingMapping.toModelMapping(), mapped)

        assertThat(readBack).isEqualTo(src)
    }
}