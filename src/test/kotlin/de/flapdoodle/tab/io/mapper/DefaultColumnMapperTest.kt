package de.flapdoodle.tab.io.mapper

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.io.MemorizingMapping
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.model.data.Column
import javafx.scene.paint.Color
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DefaultColumnMapperTest {
    @Test
    fun mapColumn() {
        val memorizingMapping = MemorizingMapping()

        val src = Column(
            name = Name("value"),
            indexType = TypeInfo.of(String::class.javaObjectType),
            valueType = TypeInfo.of(Int::class.javaObjectType),
            color = Color.AQUA,
            values = mapOf(
                "Foo" to 1,
                "Bar" to 2
            )
        )

        val testee = DefaultColumnMapper

        val mapped = testee.toFile(memorizingMapping.toFileMapping(), src)
        val readBack = testee.toModel(memorizingMapping.toModelMapping(), TypeInfo.of(String::class.javaObjectType), mapped)

        assertThat(readBack).isEqualTo(src)
    }

}