package de.flapdoodle.tab.io.mapper

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.io.MemorizingMapping
import de.flapdoodle.tab.model.calculations.Calculation
import de.flapdoodle.tab.model.calculations.adapter.Eval
import de.flapdoodle.tab.model.calculations.adapter.EvalFormulaAdapter
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
            indexType = TypeInfo.of(Int::class.javaObjectType),
            formula = EvalFormulaAdapter("x+2")
        )

        val testee = DefaultAggregationMapper()

        val mapped = testee.toFile(memorizingMapping.toFileMapping(), src)
        val readBack = testee.toModel(memorizingMapping.toModelMapping(), TypeInfo.of(Int::class.javaObjectType), mapped)

        assertThat(readBack).isEqualTo(src)
    }

}