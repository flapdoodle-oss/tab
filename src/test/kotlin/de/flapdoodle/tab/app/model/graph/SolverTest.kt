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
            listOf(Calculation.Aggregation("y", EvalAdapter("x+2"), destination))
        ).let { c -> c.connect(c.inputs[0].id, Source.ValueSource(constants.id, x.id)) })

        val source = Tab2Model(listOf(constants, formula))
        val changed = Solver.solve(source)

        assertThat(changed.node(formula.id).data(destination))
            .isEqualTo(SingleValue("y", BigDecimal::class, BigDecimal.valueOf(3), destination))

    }

    @Test
    fun singleTableConnecton() {
        val x = Column("x", Int::class, Int::class, id = ColumnId())
            .add(0, 1)
            .add(1,2)

        val constants = Node.Table<Int>(
            "table", Columns(listOf(x))
        )
        val destination = ColumnId()
        val formula = Node.Calculated<String>("calc", Calculations(
            listOf(Calculation.Tabular("y", EvalAdapter("x+2"), destination))
        ).let { c -> c.connect(c.inputs[0].id, Source.ColumnSource(constants.id, x.id)) })

        val source = Tab2Model(listOf(constants, formula))
        val changed = Solver.solve(source)

        val data = changed.node(formula.id).data(destination)
        assertThat(data)
            .isInstanceOf(Columns::class.java)

    }
}