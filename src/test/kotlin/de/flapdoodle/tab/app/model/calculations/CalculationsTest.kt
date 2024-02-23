package de.flapdoodle.tab.app.model.calculations

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.connections.Source
import de.flapdoodle.tab.app.model.data.ColumnId
import de.flapdoodle.tab.app.model.data.SingleValueId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.reflect.KClass

class CalculationsTest {
    @Test
    fun emptyCalculationsWithoutAnyInput() {
        val testee = calculations()

        assertThat(testee.inputs)
            .isEmpty()
    }

    @Test
    fun singleCalculationsWithoutAnyInput() {
        val testee = calculations(
            aggregate("x", "a*2", BigDecimal::class),
            tabular("y", "a*b", Int::class, BigDecimal::class)
        )

        assertThat(testee.inputs)
            .hasSize(2)

        val (a, b) = testee.inputs
        assertThat(a.name).isEqualTo("a")
        assertThat(b.name).isEqualTo("b")
    }

    @Test
    fun changeFormulaShouldChangeOnlyChangedInput() {
        val testee = calculations(
            aggregate("x", "a*2-c", BigDecimal::class),
            tabular("y", "a*b", Int::class, BigDecimal::class)
        )

        assertThat(testee.inputs)
            .hasSize(3)

        val (a, b, c) = testee.inputs
        assertThat(a.name).isEqualTo("a")
        assertThat(b.name).isEqualTo("b")
        assertThat(c.name).isEqualTo("c")

        val changed = testee.changeFormula(testee.list[0].id, "a*2-x")

        assertThat(changed.inputs)
            .hasSize(3)

        val (a2, b2, x) = changed.inputs
        assertThat(a2).isEqualTo(a)
        assertThat(b2).isEqualTo(b)
        assertThat(x.name).isEqualTo("x")
    }

    @Nested
    inner class Changes {

        @Test
        fun addNewVar() {
            val sourceA = Source.ValueSource(Id.Companion.nextId(Node::class), SingleValueId(String::class))

            val testee = calculations(
                aggregate("1", "a+1", BigDecimal::class),
                tabular("2", "a*2", Int::class, BigDecimal::class)
            ).let { it.copy(inputs = it.inputs.map { input -> input.copy(source = sourceA) }) }

            assertThat(testee.inputs).hasSize(1)
            val (a) = testee.inputs
            assertThat(a.mapTo).hasSize(2)

            val changed = testee.changeFormula(testee.list[0].id, "a+b")

            assertThat(changed.inputs).hasSize(2)
            val (a2, b) = changed.inputs

            assertThat(a2).isEqualTo(a)
            assertThat(a2.source).isEqualTo(sourceA)
            assertThat(b.name).isEqualTo("b")
            assertThat(b.source).isNull()
        }

        @Test
        fun renameOneVariable() {
            val sourceA = Source.ValueSource(Id.Companion.nextId(Node::class), SingleValueId(String::class))

            val testee = calculations(
                aggregate("1", "a+1", BigDecimal::class),
                tabular("2", "a*2", Int::class, BigDecimal::class)
            ).let { it.copy(inputs = it.inputs.map { input -> input.copy(source = sourceA) }) }

            assertThat(testee.inputs).hasSize(1)
            val (a) = testee.inputs
            assertThat(a.mapTo).hasSize(2)

            val changed = testee.changeFormula(testee.list[1].id, "b*2")

            assertThat(changed.inputs).hasSize(2)
            val (a2, b) = changed.inputs

            assertThat(a2.mapTo).hasSize(1)
            assertThat(a2).isEqualTo(a.copy(mapTo = a2.mapTo))
            assertThat(a2.source).isEqualTo(sourceA)
            assertThat(b.name).isEqualTo("b")
            assertThat(b.source).isEqualTo(sourceA)
        }
    }

    private fun calculations(vararg calculation: Calculation): Calculations {
        return Calculations(listOf(*calculation))
    }

    private fun <T : Any> aggregate(name: String, formula: String, type: KClass<T>): Calculation.Aggregation<T> {
        return Calculation.Aggregation(name, EvalAdapter(formula), SingleValueId(type))
    }

    private fun <K : Any, V : Any> tabular(
        name: String,
        formula: String,
        indexType: KClass<K>,
        type: KClass<V>
    ): Calculation.Tabular<K, V> {
        return Calculation.Tabular(name, EvalAdapter(formula), ColumnId(indexType, type))
    }
}