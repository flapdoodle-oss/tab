package de.flapdoodle.tab.app.io

import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.Position
import de.flapdoodle.tab.app.model.Tab2Model
import de.flapdoodle.tab.app.model.calculations.Calculation
import de.flapdoodle.tab.app.model.calculations.Calculations
import de.flapdoodle.tab.app.model.calculations.InputSlot
import de.flapdoodle.tab.app.model.calculations.adapter.EvalFormulaAdapter
import de.flapdoodle.tab.app.model.connections.Source
import de.flapdoodle.tab.app.model.data.Column
import de.flapdoodle.tab.app.model.data.Columns
import de.flapdoodle.tab.app.model.data.SingleValue
import de.flapdoodle.tab.app.model.data.SingleValues
import de.flapdoodle.tab.app.model.graph.Solver
import javafx.scene.paint.Color
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class Tab2ModelIOTest {

    @Test
    fun emptyModel() {
        val constants = Node.Constants(
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

        val table = Node.Table(
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

        val calculated = Node.Calculated(
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

        val json = Tab2ModelIO.asJson(solved)
        val readBack = Tab2ModelIO.fromJson(json)
        val jsonAgain = Tab2ModelIO.asJson(readBack)
        assertThat(json).isEqualTo(jsonAgain)
    }
}