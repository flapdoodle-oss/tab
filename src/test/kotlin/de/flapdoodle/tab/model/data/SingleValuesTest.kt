package de.flapdoodle.tab.model.data

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.Name
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SingleValuesTest {
    @Test
    fun addColumnIndexAndValue() {
        val value = SingleValue(
            Name("a"), TypeInfo.of(Int::class.javaObjectType))

        val testee = SingleValues()
            .addValue(value)
            .add(value.id, TypeInfo.of(Int::class.javaObjectType),2)

        assertThat(testee.values)
            .hasSize(1)

        val changedValue = testee.values[0]

        assertThat(changedValue.id).isEqualTo(value.id)
        assertThat(changedValue.value)
            .isEqualTo(2)

        assertThat(testee[value.id])
            .isEqualTo(2)
    }

}