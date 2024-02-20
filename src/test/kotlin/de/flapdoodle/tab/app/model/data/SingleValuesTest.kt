package de.flapdoodle.tab.app.model.data

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SingleValuesTest {
    @Test
    fun addColumnIndexAndValue() {
        val value = SingleValue("a", SingleValueId(Int::class))

        val testee = SingleValues()
            .addValue(value)
            .add(value.id, 2)

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