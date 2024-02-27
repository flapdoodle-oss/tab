package de.flapdoodle.tab.app.model.graph

import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.Tab2Model
import de.flapdoodle.tab.app.model.calculations.Calculation
import de.flapdoodle.tab.app.model.calculations.Calculations
import de.flapdoodle.tab.app.model.calculations.EvalAdapter
import de.flapdoodle.tab.app.model.connections.Source
import de.flapdoodle.tab.app.model.data.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class SolverTest {
    @Test
    fun emptyModelDoesNotTriggerAnything() {
        val result = Solver.solve(Tab2Model())
        assertThat(result.nodes).isEmpty()
    }

    @Test
    fun singleNodeWithNoConnections() {
        val source = Tab2Model(listOf(Node.Constants("const")))
        val changed = Solver.solve(source)

        assertThat(changed).isEqualTo(source)
    }

    @Test
    fun singleConnecton() {
        val x = SingleValue("x", Int::class, 1)

        val constants = Node.Constants(
            "const", SingleValues(
                listOf(x)
            )
        )
        val destination = SingleValueId()
        val formula = Node.Calculated<String>("calc", Calculations(
            listOf(Calculation.Aggregation<String>("y", EvalAdapter("x+2"), destination))
        ).let { c -> c.connect(c.inputs[0].id, Source.ValueSource(constants.id, x.id)) })

        val source = Tab2Model(listOf(constants, formula))
        val changed = Solver.solve(source)

        assertThat(changed.node(formula.id).value(destination))
            .isEqualTo(SingleValue("y", BigDecimal::class, BigDecimal.valueOf(3), destination))

    }

    @Test
    fun singleTableConnecton() {
        val x = Column("x", Int::class, Int::class)
            .add(0, 1)
            .add(1,2)
            .add(3,10)

        val constants = Node.Table<Int>(
            "table", Columns(listOf(x))
        )
        val destination = ColumnId(Int::class)
        val formula = Node.Calculated("calc", Calculations(
            listOf(Calculation.Tabular("y", EvalAdapter("x+2"), destination))
        ).let { c -> c.connect(c.inputs[0].id, Source.ColumnSource(constants.id, x.id)) })

        val source = Tab2Model(listOf(constants, formula))
        val changed = Solver.solve(source)

        val data = changed.node(formula.id).column(destination)
        assertThat(data.values)
            .isEqualTo(mapOf(
                0 to BigDecimal.valueOf(3),
                1 to BigDecimal.valueOf(4),
                3 to BigDecimal.valueOf(12)
            ))

    }
}