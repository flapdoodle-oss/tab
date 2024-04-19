package de.flapdoodle.tab.app.io.mapper

import de.flapdoodle.tab.app.io.MemorizingMapping
import de.flapdoodle.tab.app.model.calculations.Calculation
import de.flapdoodle.tab.app.model.calculations.adapter.Eval
import de.flapdoodle.tab.app.model.calculations.adapter.EvalFormulaAdapter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DefaultAggregationMapperTest {

    @Test
    fun testEvalFormulaAdapterEquals() {
        val testee = EvalFormulaAdapter("x+2")
        assertThat(testee)
            .isEqualTo(testee.copy(expression = Eval.parse("x+2")))
    }

    @Test
    fun mapAggregation() {
        val memorizingMapping = MemorizingMapping()

        val src = Calculation.Aggregation(
            name = "name",
            indexType = Int::class,
            formula = EvalFormulaAdapter("x+2")
        )

        val testee = DefaultAggregationMapper()

        val mapped = testee.toFile(memorizingMapping.toFileMapping(), src)
        val readBack = testee.toModel(memorizingMapping.toModelMapping(), Int::class, mapped)

        assertThat(readBack).isEqualTo(src)
    }

}