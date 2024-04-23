package de.flapdoodle.tab.io.mapper

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.io.MemorizingMapping
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.Position
import de.flapdoodle.tab.model.calculations.Calculation
import de.flapdoodle.tab.model.calculations.Calculations
import de.flapdoodle.tab.model.calculations.InputSlot
import de.flapdoodle.tab.model.calculations.adapter.EvalFormulaAdapter
import de.flapdoodle.tab.model.connections.Source
import de.flapdoodle.tab.model.data.ColumnId
import de.flapdoodle.tab.model.data.SingleValueId
import de.flapdoodle.tab.model.graph.Solver
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DefaultCalculatedMapperTest {
    @Test
    fun mapCalculated() {
        val memorizingMapping = MemorizingMapping()

        val src = de.flapdoodle.tab.model.Node.Calculated(
            name = "name",
            indexType = Int::class,
            position = Position(10.0, 20.0)
        )

        val testee = DefaultCalculatedMapper()

        val mapped = testee.toFile(memorizingMapping.toFileMapping(), src)
        val readBack = testee.toModel(memorizingMapping.toModelMapping(), mapped)

        assertThat(readBack).isEqualTo(src)
    }

    @Test
    fun sample() {
        val memorizingMapping = MemorizingMapping()

        val src = de.flapdoodle.tab.model.Node.Calculated(
            name = "calculated",
            indexType = Int::class,
            calculations = Calculations(
                indexType = Int::class,
                aggregations = listOf(
                    Calculation.Aggregation(
                        name = "x+2",
                        indexType = Int::class,
                        formula = EvalFormulaAdapter("x+2")
                    )
                ),
                tabular = listOf(
                    Calculation.Tabular(
                        name = "x+y",
                        indexType = Int::class,
                        formula = EvalFormulaAdapter("x+y")
                    )
                ),
                inputs = listOf(
                    InputSlot(
                        name = "x",
                        mapTo = (Calculation.Aggregation(
                            name = "x+2",
                            indexType = Int::class,
                            formula = EvalFormulaAdapter("x+2")
                        ).variables() + Calculation.Tabular(
                            name = "x+y",
                            indexType = Int::class,
                            formula = EvalFormulaAdapter("x+y")
                        ).variables()).filter {
                            it.name=="x"
                        }.toSet(),
                        source = Source.ValueSource(Id.nextId(de.flapdoodle.tab.model.Node.Constants::class), SingleValueId())
                    ),
                    InputSlot(
                        name = "y",
                        mapTo = Calculation.Tabular(
                            name = "x+y",
                            indexType = Int::class,
                            formula = EvalFormulaAdapter("x+y")
                        ).variables().filter { it.name == "y" }.toSet(),
                        source = Source.ColumnSource(Id.nextId(de.flapdoodle.tab.model.Node.Table::class), ColumnId(), Int::class)
                    )
                )
            )
        )

        val testee = DefaultCalculatedMapper()

        val mapped = testee.toFile(memorizingMapping.toFileMapping(), src)
        val readBack = testee.toModel(memorizingMapping.toModelMapping(), mapped)

        assertThat(readBack).isEqualTo(src)
    }
}