package de.flapdoodle.tab.model.data

import de.flapdoodle.tab.model.data.SingleValue
import de.flapdoodle.tab.model.data.SingleValues
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SingleValuesTest {
    @Test
    fun addColumnIndexAndValue() {
        val value = SingleValue("a", Int::class)

        val testee = SingleValues()
            .addValue(value)
            .add(value.id, Int::class,2)

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