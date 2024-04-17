package de.flapdoodle.tab.app.io.mapper

import de.flapdoodle.tab.app.io.adapter.ToFileMapping
import de.flapdoodle.tab.app.io.adapter.ToModelMapping
import org.assertj.core.api.Assertions.assertThat

data class StaticTestMapper<T: Any, F: Any>(
    val toModel: T,
    val toFile: F
) : Mapper<T, F> {

    override fun toFile(toFileMapping: ToFileMapping, src: T): F {
        assertThat(src).isEqualTo(toModel)
        return toFile
    }

    override fun toModel(toModelMapping: ToModelMapping, src: F): T {
        assertThat(src).isEqualTo(toFile)
        return toModel
    }
}