package de.flapdoodle.tab.io.mapper

import de.flapdoodle.tab.io.MemorizingMapping
import de.flapdoodle.tab.io.file.FileNode
import de.flapdoodle.tab.io.file.Tab2File
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.Position
import de.flapdoodle.tab.model.Tab2Model
import de.flapdoodle.tab.model.calculations.Calculation
import de.flapdoodle.tab.model.calculations.Calculations
import de.flapdoodle.tab.model.calculations.InputSlot
import de.flapdoodle.tab.model.calculations.adapter.EvalFormulaAdapter
import de.flapdoodle.tab.model.connections.Source
import de.flapdoodle.tab.model.data.Column
import de.flapdoodle.tab.model.data.Columns
import de.flapdoodle.tab.model.data.SingleValue
import de.flapdoodle.tab.model.data.SingleValues
import de.flapdoodle.tab.model.graph.Solver
import javafx.scene.paint.Color
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DefaultModelFileMapperTest {

    @Test
    fun testDelegation() {
        val testNode = de.flapdoodle.tab.model.Node.Constants(name = "in")
        val testFileNode = FileNode(
            name ="out",
            position = Position(0.0, 0.0),
            id = "id"
        )

        val testee = DefaultModelFileMapper(nodeMapper = StaticTestMapper(testNode, testFileNode))

        val result = testee.toFile(MemorizingMapping().toFileMapping(), Tab2Model(listOf(testNode)))
        assertThat(result.nodes)
            .hasSize(1)
            .containsExactly(testFileNode)

        val readBack = testee.toModel(MemorizingMapping().toModelMapping(), Tab2File(listOf(testFileNode)))
        assertThat(readBack.nodes)
            .hasSize(1)
            .containsExactly(testNode)
    }

    @Test
    fun sample() {
        val memorizingMapping = MemorizingMapping()

        val constants = de.flapdoodle.tab.model.Node.Constants(
            name = "const",
            values = SingleValues(listOf(
                SingleValue(
                    name = "a",
                    valueType = Int::class,
                    value = 2,
                    color = Color.RED
                )
            )),
            position = Position(1.0, 2.0)
        )

        val table = de.flapdoodle.tab.model.Node.Table(
            name = "table",
            indexType = Int::class,
            columns = Columns(listOf(
                Column(
                    name = "b",
                    indexType = Int::class,
                    valueType = Double::class,
                    values = mapOf(1 to 1.0, 2 to 2.0),
                    color = Color.RED
                )
            )),
            position = Position(2.0, 3.0)
        )

        val aggregation = Calculation.Aggregation(
            name = "x+2",
            indexType = Int::class,
            formula = EvalFormulaAdapter("x+2")
        )
        val tabular = Calculation.Tabular(
            name = "x+y",
            indexType = Int::class,
            formula = EvalFormulaAdapter("x+y")
        )

        val calculated = de.flapdoodle.tab.model.Node.Calculated(
            name = "calculated",
            indexType = Int::class,
            calculations = Calculations(
                indexType = Int::class,
                aggregations = listOf(
                    aggregation
                ),
                tabular = listOf(
                    tabular
                ),
                inputs = listOf(
                    InputSlot(
                        name = "x",
                        mapTo = (aggregation.variables() + tabular.variables()).filter {
                            it.name=="x"
                        }.toSet(),
                        source = Source.ValueSource(constants.id, constants.values.values[0].id)
                    ),
                    InputSlot(
                        name = "y",
                        mapTo = tabular.variables().filter { it.name=="y" }.toSet(),
                        source = Source.ColumnSource(table.id, table.columns.columns()[0].id, Int::class)
                    )
                )
            )
        )

        val src = Tab2Model(
            nodes = listOf(
                constants,
                table,
                calculated
            )
        )

        val solved = Solver.solve(src)

        val testee = DefaultModelFileMapper()

        val result = testee.toFile(memorizingMapping.toFileMapping(), solved)
        val readBack = testee.toModel(memorizingMapping.toModelMapping(), result)

        assertThat(readBack).isEqualTo(solved)
    }
}