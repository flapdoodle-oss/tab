package de.flapdoodle.tab.app.io.mapper

import de.flapdoodle.tab.app.io.MemorizingMapping
import de.flapdoodle.tab.app.model.data.Column
import javafx.scene.paint.Color
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DefaultColumnMapperTest {
    @Test
    fun mapColumn() {
        val memorizingMapping = MemorizingMapping()

        val src = Column(
            name = "value",
            indexType = String::class,
            valueType = Int::class,
            color = Color.AQUA,
            values = mapOf(
                "Foo" to 1,
                "Bar" to 2
            )
        )

        val testee = DefaultColumnMapper

        val mapped = testee.toFile(memorizingMapping.toFileMapping(), src)
        val readBack = testee.toModel(memorizingMapping.toModelMapping(), String::class, mapped)

        assertThat(readBack).isEqualTo(src)
    }

}