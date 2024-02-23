package de.flapdoodle.tab.app.model.calculations

import de.flapdoodle.tab.app.model.data.ColumnId
import de.flapdoodle.tab.app.model.data.SingleValueId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.reflect.KClass

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
                aggregate("x","a*2", BigDecimal::class),
                tabular("y","a*b", Int::class, BigDecimal::class)
            )
        )

        assertThat(testee.inputs())
            .hasSize(2)

        val (a,b) = testee.inputs().toList()
        assertThat(a.name).isEqualTo("a")
        assertThat(b.name).isEqualTo("b")
    }

    @Test
    fun changeFormulaShouldChangeOnlyChangedInput() {
        val testee = Calculations(
            list = listOf(
                aggregate("x","a*2-c", BigDecimal::class),
                tabular("y","a*b", Int::class, BigDecimal::class)
            )
        )

        assertThat(testee.inputs())
            .hasSize(3)

        val (a,b,c) = testee.inputs().toList()
        assertThat(a.name).isEqualTo("a")
        assertThat(b.name).isEqualTo("b")
        assertThat(c.name).isEqualTo("c")

        val changed = testee.changeFormula(testee.list[0].id,"a*2-C")

        assertThat(changed.inputs())
            .hasSize(3)

        val (a2,b2,c2) = changed.inputs().toList()
        assertThat(a2).isEqualTo(a)
        assertThat(b2).isEqualTo(b)
        assertThat(c.name).isEqualTo("C")
    }

    private fun <T: Any> aggregate(name: String, formula: String, type: KClass<T>): Calculation.Aggregation<T> {
        return Calculation.Aggregation(name, EvalAdapter(formula), SingleValueId(type))
    }

    private fun <K: Any, V: Any> tabular(name: String, formula: String, indexType: KClass<K>, type: KClass<V>): Calculation.Tabular<K, V> {
        return Calculation.Tabular(name, EvalAdapter(formula), ColumnId(indexType, type))
    }
}