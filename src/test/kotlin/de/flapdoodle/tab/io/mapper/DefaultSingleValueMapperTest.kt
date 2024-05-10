package de.flapdoodle.tab.io.mapper

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.io.MemorizingMapping
import de.flapdoodle.tab.model.data.SingleValue
import javafx.scene.paint.Color
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DefaultSingleValueMapperTest {

    @Test
    fun mapSingleValue() {
        val memorizingMapping = MemorizingMapping()

        val src = SingleValue(
            name = "value",
            valueType = TypeInfo.of(Double::class.javaObjectType),
            value = 1231.2,
            color = Color.RED
        )

        val testee = DefaultSingleValueMapper
        
        val mapped = testee.toFile(memorizingMapping.toFileMapping(), src)
        val readBack = testee.toModel(memorizingMapping.toModelMapping(), mapped)

        assertThat(readBack).isEqualTo(src)
    }

}