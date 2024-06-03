package de.flapdoodle.tab.io

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.*
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
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TabModelIOTest {

    @Test
    fun emptyModel() {
        val constants = de.flapdoodle.tab.model.Node.Constants(
            name = Title("const"),
            values = SingleValues(listOf(
                SingleValue(
                    name = Name("a"),
                    valueType = TypeInfo.of(Int::class.javaObjectType),
                    value = 2,
                    color = Color.RED
                )
            )),
            position = Position(1.0, 2.0)
        )

        val table = de.flapdoodle.tab.model.Node.Table(
            name = Title("table"),
            indexType = TypeInfo.of(Int::class.javaObjectType),
            columns = Columns(listOf(
                Column(
                    name = "b",
                    indexType = TypeInfo.of(Int::class.javaObjectType),
                    valueType = TypeInfo.of(Double::class.javaObjectType),
                    values = mapOf(1 to 1.0, 2 to 2.0),
                    color = Color.RED
                )
            )),
            position = Position(2.0, 3.0)
        )

        val aggregation = Calculation.Aggregation(
            name = "x+2",
            indexType = TypeInfo.of(Int::class.javaObjectType),
            formula = EvalFormulaAdapter("x+2")
        )
        val tabular = Calculation.Tabular(
            name = "x+y",
            indexType = TypeInfo.of(Int::class.javaObjectType),
            formula = EvalFormulaAdapter("x+y")
        )

        val calculated = de.flapdoodle.tab.model.Node.Calculated(
            name = Title("calculated"),
            indexType = TypeInfo.of(Int::class.javaObjectType),
            calculations = Calculations(
                indexType = TypeInfo.of(Int::class.javaObjectType),
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
                        source = Source.ColumnSource(table.id, table.columns.columns()[0].id, TypeInfo.of(Int::class.javaObjectType))
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

        val json = de.flapdoodle.tab.io.Tab2ModelIO.asJson(solved)
        val readBack = de.flapdoodle.tab.io.Tab2ModelIO.fromJson(json)
        val jsonAgain = de.flapdoodle.tab.io.Tab2ModelIO.asJson(readBack)
        assertThat(json).isEqualTo(jsonAgain)
    }
}