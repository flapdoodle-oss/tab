package de.flapdoodle.tab.app.model.calculations

import de.flapdoodle.tab.app.model.data.SingleValueId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class CalculationsTest {
    @Test
    fun emptyCalculationsWithoutAnyInput() {
        val testee = Calculations()

        assertThat(testee.inputs())
            .isEmpty()
    }

    @Test
    fun singleCalculationsWithoutAnyInput() {
        val testee = Calculations(
            list = listOf(
                Calculation.Aggregation(
                    name = "x",
                    formula = EvalAdapter("a*2"),
                    destination = SingleValueId(BigDecimal::class)
                ),
                Calculation.Aggregation(
                    name = "y",
                    formula = EvalAdapter("a*b"),
                    destination = SingleValueId(BigDecimal::class)
                )
            )
        )

        assertThat(testee.inputs())
            .hasSize(2)

        val (a,b) = testee.inputs().toList()
        assertThat(a.name).isEqualTo("a")
        assertThat(b.name).isEqualTo("b")
    }
}