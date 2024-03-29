package de.flapdoodle.tab.app.model.graph

import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.Tab2Model
import de.flapdoodle.tab.app.model.calculations.Calculation
import de.flapdoodle.tab.app.model.calculations.Calculations
import de.flapdoodle.tab.app.model.calculations.adapter.EvalFormulaAdapter
import de.flapdoodle.tab.app.model.connections.Source
import de.flapdoodle.tab.app.model.data.*
import de.flapdoodle.types.Either
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigInteger

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
        val formula = Node.Calculated("calc", String::class, Calculations(
            listOf(Calculation.Aggregation<String>("y", EvalFormulaAdapter("x+2"), destination))
        ).let { c -> c.connect(c.inputs()[0].id, Source.ValueSource(constants.id, x.id)) })

        val source = Tab2Model(listOf(constants, formula))
        val changed = Solver.solve(source)

        assertThat(changed.node(formula.id).value(destination))
            .isEqualTo(SingleValue("y", BigInteger::class, BigInteger.valueOf(3), destination))

    }

    @Test
    fun singleTableConnection() {
        val x = Column("x", Int::class, Int::class)
            .add(0, 1)
            .add(1,2)
            .add(3,10)

        val table = Node.Table(
            "table", Int::class, Columns(listOf(x))
        )
        val destination = ColumnId(Int::class)
        val formula = Node.Calculated("calc", Int::class, Calculations(
            tabular = listOf(Calculation.Tabular("y", EvalFormulaAdapter("x+2"), destination))
        ).let { c -> c.connect(c.inputs()[0].id, Source.ColumnSource(table.id, x.id)) })

        val source = Tab2Model(listOf(table, formula))
        val changed = Solver.solve(source)

        val data = changed.node(formula.id).column(destination)
        assertThat(data.values)
            .isEqualTo(mapOf(
                0 to BigInteger.valueOf(3),
                1 to BigInteger.valueOf(4),
                3 to BigInteger.valueOf(12)
            ))

    }

    @Test
    fun singleTableConnectionReconnected() {
        val x = Column("x", Int::class, Int::class)
            .add(0, 1)
            .add(1,2)
            .add(3,10)

        val y = Column("y", Int::class, Int::class)
            .add(0, 2)
            .add(1,4)
            .add(3,20)

        val table = Node.Table(
            "table", Int::class, Columns(listOf(x, y))
        )
        val destination = ColumnId(Int::class)
        val formula = Node.Calculated("calc", Int::class, Calculations(
            tabular = listOf(Calculation.Tabular("y", EvalFormulaAdapter("x+2"), destination))
        ).let { c -> c.connect(c.inputs()[0].id, Source.ColumnSource(table.id, x.id)) })

        val source = Tab2Model(listOf(table, formula))
        val reconnected = source.connect(table.id, Either.left(y.id), formula.id, Either.right(formula.calculations.inputs()[0].id))
        val changed = Solver.solve(reconnected)

        val data = changed.node(formula.id).column(destination)
        assertThat(data.values)
            .isEqualTo(mapOf(
                0 to BigInteger.valueOf(4),
                1 to BigInteger.valueOf(6),
                3 to BigInteger.valueOf(22)
            ))

    }

    @Test
    fun singleTableReconnectToDifferentType() {
        val x = Column("x", Int::class, Int::class)
            .add(0, 1)
            .add(1,2)
            .add(3,10)

        val y = Column("y", Int::class, String::class)
            .add(0, "2")
            .add(1,"4")
            .add(3,"20")

        val table = Node.Table(
            "table", Int::class, Columns(listOf(x, y))
        )
        val destination = ColumnId(Int::class)
        val formula = Node.Calculated("calc", Int::class, Calculations(
            tabular = listOf(Calculation.Tabular("y", EvalFormulaAdapter("x"), destination))
        ).let { c -> c.connect(c.inputs()[0].id, Source.ColumnSource(table.id, x.id)) })

        val source = Tab2Model(listOf(table, formula))
        val reconnected = source.connect(table.id, Either.left(y.id), formula.id, Either.right(formula.calculations.inputs()[0].id))
        val changed = Solver.solve(reconnected)

        val data = changed.node(formula.id).column(destination)
        assertThat(data.values)
            .isEqualTo(mapOf(
                0 to "2",
                1 to "4",
                3 to "20",
            ))

    }

    @Test
    fun tablesAndValues() {
        val a = Column("a", Int::class, Int::class)
            .add(0, 1)
            .add(1,2)
            .add(3,10)

        val b = Column("b", Int::class, Int::class)
            .add(0, 5)
            .add(2,10)
            .add(3,5)

        val x = SingleValue("x", Int::class, 1)

        val constants = Node.Constants(
            "const", SingleValues(
                listOf(x)
            )
        )

        val table = Node.Table(
            "table", Int::class, Columns(listOf(a, b))
        )
        val destination = ColumnId(Int::class)
        val formula = Node.Calculated("calc", Int::class, Calculations(
            tabular = listOf(Calculation.Tabular("y", EvalFormulaAdapter("x+b+c"), destination))
        ).let { c ->
            c.connect(c.inputs()[0].id, Source.ColumnSource(table.id, a.id))
                .connect(c.inputs()[1].id, Source.ColumnSource(table.id, b.id))
                .connect(c.inputs()[2].id, Source.ValueSource(constants.id, x.id))
        })

        val source = Tab2Model(listOf(constants, table, formula))
        val changed = Solver.solve(source)

        val data = changed.node(formula.id).column(destination)
        assertThat(data.values)
            .isEqualTo(mapOf(
                0 to 7,
                1 to 8,
                2 to 13,
                3 to 16
            ))

    }
}