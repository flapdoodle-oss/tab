package de.flapdoodle.tab.app.model.calculations

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.connections.Source
import de.flapdoodle.tab.app.model.data.ColumnId
import de.flapdoodle.tab.app.model.data.SingleValueId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CalculationsTest {
    @Test
    fun emptyCalculationsWithoutAnyInput() {
        val testee = calculations<String>()

        assertThat(testee.inputs)
            .isEmpty()
    }

    @Test
    fun singleCalculationsWithoutAnyInput() {
        val testee = calculations(
            aggregate("x", "a*2"),
            tabular("y", "a*b", ColumnId(Int::class))
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
            aggregate("x", "a*2-c"),
            tabular("y", "a*b", ColumnId(Int::class))
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
            val sourceA = Source.ValueSource(Id.Companion.nextId(Node::class), SingleValueId())

            val testee = calculations(
                aggregate("1", "a+1"),
                tabular("2", "a*2", ColumnId(Int::class))
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
            val sourceA = Source.ValueSource(Id.Companion.nextId(Node::class), SingleValueId())

            val testee = calculations(
                aggregate("1", "a+1"),
                tabular("2", "a*2", ColumnId(Int::class))
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

        @Test
        fun removeVar() {
            val sourceA = Source.ValueSource(Id.Companion.nextId(Node::class), SingleValueId())

            val testee = calculations(
                aggregate("1", "a+1"),
                tabular("2", "a+b", ColumnId(Int::class))
            ).let { it.copy(inputs = it.inputs.map { input -> input.copy(source = sourceA) }) }

            assertThat(testee.inputs).hasSize(2)
            val (a, b) = testee.inputs
            assertThat(a.mapTo).hasSize(2)
            assertThat(b.name).isEqualTo("b")
            assertThat(b.source).isEqualTo(sourceA)

            val changed = testee.changeFormula(testee.list[1].id, "a*2")

            assertThat(changed.inputs).hasSize(1)
            val (a2) = changed.inputs

            assertThat(a2).isEqualTo(a)
            assertThat(a2.source).isEqualTo(sourceA)
        }
    }

    private fun <K: Comparable<K>> calculations(vararg calculation: Calculation<K>): Calculations<K> {
        return Calculations(listOf(*calculation))
    }

    private fun <K: Comparable<K>> aggregate(name: String, formula: String): Calculation.Aggregation<K> {
        return Calculation.Aggregation(name, EvalAdapter(formula), SingleValueId())
    }

    private fun <K: Comparable<K>> tabular(
        name: String,
        formula: String,
        columnId: ColumnId<K>
    ): Calculation.Tabular<K> {
        return Calculation.Tabular(name, EvalAdapter(formula), columnId)
    }
}