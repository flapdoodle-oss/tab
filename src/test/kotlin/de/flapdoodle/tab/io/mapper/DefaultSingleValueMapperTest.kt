package de.flapdoodle.tab.io.mapper

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
            valueType = Int::class,
            value = 1231,
            color = Color.RED
        )

        val testee = DefaultSingleValueMapper
        
        val mapped = DefaultSingleValueMapper.toFile(memorizingMapping.toFileMapping(), src)
        val readBack = DefaultSingleValueMapper.toModel(memorizingMapping.toModelMapping(), mapped)

        assertThat(readBack).isEqualTo(src)
    }

}