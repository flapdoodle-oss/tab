package de.flapdoodle.tab.model.calculations

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.calculations.Calculation
import de.flapdoodle.tab.model.calculations.Calculations
import de.flapdoodle.tab.model.calculations.adapter.EvalFormulaAdapter
import de.flapdoodle.tab.model.connections.Source
import de.flapdoodle.tab.model.data.ColumnId
import de.flapdoodle.tab.model.data.SingleValueId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass

class CalculationsTest {
    @Test
    fun emptyCalculationsWithoutAnyInput() {
        val testee = calculations<String>(String::class)

        assertThat(testee.inputs())
            .isEmpty()
    }

    @Test
    fun singleCalculationsWithoutAnyInput() {
        val testee = calculations(Int::class,
            aggregate(Int::class, "x", "a*2"),
            tabular(Int::class,"y", "a*b", ColumnId())
        )

        assertThat(testee.inputs())
            .hasSize(2)

        val (a, b) = testee.inputs()
        assertThat(a.name).isEqualTo("a")
        assertThat(b.name).isEqualTo("b")
    }

    @Test
    fun changeFormulaShouldChangeOnlyChangedInput() {
        val testee = calculations(Int::class,
            aggregate(Int::class,"x", "a*2-c"),
            tabular(Int::class,"y", "a*b", ColumnId())
        )

        assertThat(testee.inputs())
            .hasSize(3)

        val (a, b, c) = testee.inputs()
        assertThat(a.name).isEqualTo("a")
        assertThat(b.name).isEqualTo("b")
        assertThat(c.name).isEqualTo("c")

        val changed = testee.changeFormula(testee.aggregations()[0].id, "a*2-x")

        assertThat(changed.inputs())
            .hasSize(3)

        val (a2, b2, x) = changed.inputs()
        assertThat(a2).isEqualTo(a)
        assertThat(b2).isEqualTo(b)
        assertThat(x.name).isEqualTo("x")
    }

    @Nested
    inner class Changes {

        @Test
        fun addNewVar() {
            val sourceA = Source.ValueSource(Id.Companion.nextId(de.flapdoodle.tab.model.Node::class), SingleValueId())

            val testee = calculations(Int::class,
                aggregate(Int::class,"1", "a+1"),
                tabular(Int::class,"2", "a*2", ColumnId())
            ).let { it.copy(inputs = it.inputs().map { input -> input.copy(source = sourceA) }) }

            assertThat(testee.inputs()).hasSize(1)
            val (a) = testee.inputs()
            assertThat(a.mapTo).hasSize(2)

            val changed = testee.changeFormula(testee.aggregations()[0].id, "a+b")

            assertThat(changed.inputs()).hasSize(2)
            val (a2, b) = changed.inputs()

            assertThat(a2).isEqualTo(a)
            assertThat(a2.source).isEqualTo(sourceA)
            assertThat(b.name).isEqualTo("b")
            assertThat(b.source).isNull()
        }

        @Test
        fun renameOneVariable() {
            val sourceA = Source.ValueSource(Id.Companion.nextId(de.flapdoodle.tab.model.Node::class), SingleValueId())

            val testee = calculations(Int::class,
                aggregate(Int::class,"1", "a+1"),
                tabular(Int::class,"2", "a*2", ColumnId())
            ).let { it.copy(inputs = it.inputs().map { input -> input.copy(source = sourceA) }) }

            assertThat(testee.inputs()).hasSize(1)
            val (a) = testee.inputs()
            assertThat(a.mapTo).hasSize(2)

            val changed = testee.changeFormula(testee.tabular()[0].id, "b*2")

            assertThat(changed.inputs()).hasSize(2)
            val (a2, b) = changed.inputs()

            assertThat(a2.mapTo).hasSize(1)
            assertThat(a2).isEqualTo(a.copy(mapTo = a2.mapTo))
            assertThat(a2.source).isEqualTo(sourceA)
            assertThat(b.name).isEqualTo("b")
            assertThat(b.source).isEqualTo(sourceA)
        }

        @Test
        fun removeVar() {
            val sourceA = Source.ValueSource(Id.Companion.nextId(de.flapdoodle.tab.model.Node::class), SingleValueId())

            val testee = calculations(Int::class,
                aggregate(Int::class,"1", "a+1"),
                tabular(Int::class,"2", "a+b", ColumnId())
            ).let { it.copy(inputs = it.inputs().map { input -> input.copy(source = sourceA) }) }

            assertThat(testee.inputs()).hasSize(2)
            val (a, b) = testee.inputs()
            assertThat(a.mapTo).hasSize(2)
            assertThat(b.name).isEqualTo("b")
            assertThat(b.source).isEqualTo(sourceA)

            assertThat(testee.tabular()).hasSize(1)

            val changed = testee.changeFormula(testee.tabular()[0].id, "a*2")

            assertThat(changed.inputs()).hasSize(1)
            val (a2) = changed.inputs()

            assertThat(a2).isEqualTo(a)
            assertThat(a2.source).isEqualTo(sourceA)
        }
    }

    private fun <K: Comparable<K>> calculations(indexType: KClass<K>, vararg calculation: Calculation<K>): Calculations<K> {
        val agg = calculation.filterIsInstance<Calculation.Aggregation<K>>()
        val tab = calculation.filterIsInstance<Calculation.Tabular<K>>()
        return Calculations(indexType, aggregations = agg, tabular = tab)
    }

    private fun <K: Comparable<K>> aggregate(indexType: KClass<K>, name: String, formula: String): Calculation.Aggregation<K> {
        return Calculation.Aggregation(indexType, name, EvalFormulaAdapter(formula), SingleValueId())
    }

    private fun <K: Comparable<K>> tabular(
        indexType: KClass<K>,
        name: String,
        formula: String,
        columnId: ColumnId
    ): Calculation.Tabular<K> {
        return Calculation.Tabular(indexType, name, EvalFormulaAdapter(formula), columnId)
    }
}