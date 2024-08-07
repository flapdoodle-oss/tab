package graph

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.Model
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.Title
import de.flapdoodle.tab.model.calculations.Calculation
import de.flapdoodle.tab.model.calculations.Calculations
import de.flapdoodle.tab.model.calculations.adapter.EvalFormulaAdapter
import de.flapdoodle.tab.model.calculations.interpolation.InterpolationType
import de.flapdoodle.tab.model.changes.Change
import de.flapdoodle.tab.model.connections.Source
import de.flapdoodle.tab.model.data.*
import de.flapdoodle.tab.model.graph.Solver
import de.flapdoodle.types.Either
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigInteger

class SolverTest {
    @Test
    fun emptyModelDoesNotTriggerAnything() {
        val result = Solver.solve(Model())
        assertThat(result.nodes()).isEmpty()
    }

    @Test
    fun singleNodeWithNoConnections() {
        val source = Model(nodes = listOf(Node.Constants(Title("const"))))
        val changed = Solver.solve(source)

        assertThat(changed).isEqualTo(source)
    }

    @Test
    fun singleConnecton() {
        val x = SingleValue(Name("x"), TypeInfo.of(Int::class.javaObjectType), 1)

        val constants = Node.Constants(
            Title("const"), SingleValues(
                listOf(x)
            )
        )
        val destination = SingleValueId()
        val formula = Node.Calculated(Title("calc"), TypeInfo.of(String::class.javaObjectType), Calculations(TypeInfo.of(String::class.javaObjectType),
            listOf(Calculation.Aggregation<String>(
                TypeInfo.of(String::class.javaObjectType),
                Name("y"),
                EvalFormulaAdapter("x+2"),
                destination = destination
            ))
        ).let { c -> c.connect(c.inputs()[0].id, Source.ValueSource(constants.id, x.id)) })

        val source = Model(nodes = listOf(constants, formula))
        val changed = Solver.solve(source)

        assertThat(changed.node(formula.id).value(destination))
            .isEqualTo(SingleValue(Name("y"), TypeInfo.of(BigInteger::class.javaObjectType), BigInteger.valueOf(3), destination))

    }

    @Test
    fun singleTableConnection() {
        val x = Column(Name("x"), TypeInfo.of(Int::class.javaObjectType), TypeInfo.of(Int::class.javaObjectType))
            .add(0, 1)
            .add(1,2)
            .add(3,10)

        val table = Node.Table(
            Title("table"), TypeInfo.of(Int::class.javaObjectType), Columns(listOf(x))
        )
        val destination = ColumnId()
        val formula = Node.Calculated(Title("calc"), TypeInfo.of(Int::class.javaObjectType), Calculations(TypeInfo.of(Int::class.javaObjectType),
            tabular = listOf(Calculation.Tabular(
                TypeInfo.of(Int::class.javaObjectType),
                Name("y"),
                EvalFormulaAdapter("x+2"),
                interpolationType = InterpolationType.Linear,
                destination = destination
            ))
        ).let { c -> c.connect(c.inputs()[0].id, Source.ColumnSource(table.id, x.id, TypeInfo.of(Int::class.javaObjectType))) })

        val source = Model(listOf(table, formula))
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
    fun tableAggregation() {
        val x = Column(Name("x"), TypeInfo.of(Int::class.javaObjectType), TypeInfo.of(Int::class.javaObjectType))
            .add(0, 1)
            .add(1,2)
            .add(3,10)

        val table = Node.Table(
            Title("table"), TypeInfo.of(Int::class.javaObjectType), Columns(listOf(x))
        )
        val destination = SingleValueId()
        val formula = Node.Calculated(Title("calc"), TypeInfo.of(Int::class.javaObjectType), Calculations(TypeInfo.of(Int::class.javaObjectType),
            aggregations = listOf(Calculation.Aggregation<Int>(
                TypeInfo.of(Int::class.javaObjectType),
                Name("y"),
                EvalFormulaAdapter("sum(x)"),
                destination = destination
            ))
        ).let { c -> c.connect(c.inputs()[0].id, Source.ColumnSource(table.id, x.id, TypeInfo.of(Int::class.javaObjectType))) })

        val source = Model(listOf(table, formula))
        val changed = Solver.solve(source)

        val data = changed.node(formula.id).value(destination)
        assertThat(data.value)
            .isEqualTo(13)

    }

    @Test
    fun singleTableConnectionReconnected() {
        val x = Column(Name("x"), TypeInfo.of(Int::class.javaObjectType), TypeInfo.of(Int::class.javaObjectType))
            .add(0, 1)
            .add(1,2)
            .add(3,10)

        val y = Column(Name("y"), TypeInfo.of(Int::class.javaObjectType), TypeInfo.of(Int::class.javaObjectType))
            .add(0, 2)
            .add(1,4)
            .add(3,20)

        val table = Node.Table(
            Title("table"), TypeInfo.of(Int::class.javaObjectType), Columns(listOf(x, y))
        )
        val destination = ColumnId()
        val formula = Node.Calculated(Title("calc"), TypeInfo.of(Int::class.javaObjectType), Calculations(TypeInfo.of(Int::class.javaObjectType),
            tabular = listOf(Calculation.Tabular(
                TypeInfo.of(Int::class.javaObjectType),
                Name("y"),
                EvalFormulaAdapter("x+2"),
                interpolationType = InterpolationType.Linear,
                destination = destination
            ))
        ).let { c -> c.connect(c.inputs()[0].id, Source.ColumnSource(table.id, x.id, TypeInfo.of(Int::class.javaObjectType))) })

        val source = Model(listOf(table, formula))
        val reconnected = source.apply(Change.Connect(table.id, Either.left(y.id), formula.id, Either.right(formula.calculations.inputs()[0].id)))
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
        val x = Column(Name("x"), TypeInfo.of(Int::class.javaObjectType), TypeInfo.of(Int::class.javaObjectType))
            .add(0, 1)
            .add(1,2)
            .add(3,10)

        val y = Column(Name("y"), TypeInfo.of(Int::class.javaObjectType), TypeInfo.of(String::class.javaObjectType))
            .add(0, "2")
            .add(1,"4")
            .add(3,"20")

        val table = Node.Table(
            Title("table"), TypeInfo.of(Int::class.javaObjectType), Columns(listOf(x, y))
        )
        val destination = ColumnId()
        val formula = Node.Calculated(Title("calc"), TypeInfo.of(Int::class.javaObjectType), Calculations(TypeInfo.of(Int::class.javaObjectType),
            tabular = listOf(Calculation.Tabular(
                TypeInfo.of(Int::class.javaObjectType),
                Name("y"),
                EvalFormulaAdapter("x"),
                interpolationType = InterpolationType.Linear,
                destination = destination
            ))
        ).let { c -> c.connect(c.inputs()[0].id, Source.ColumnSource(table.id, x.id, TypeInfo.of(Int::class.javaObjectType))) })

        val source = Model(listOf(table, formula))
        val reconnected = source.apply(Change.Connect(table.id, Either.left(y.id), formula.id, Either.right(formula.calculations.inputs()[0].id)))
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
        val a = Column(
            name = Name("a"),
            indexType = TypeInfo.of(Int::class.javaObjectType),
            valueType = TypeInfo.of(Int::class.javaObjectType),
            interpolationType = InterpolationType.LastValue
        )
            .add(0, 1)
            .add(1,2)
            .add(3,10)

        val b = Column(
            name = Name("b"),
            indexType = TypeInfo.of(Int::class.javaObjectType),
            valueType = TypeInfo.of(Int::class.javaObjectType),
            interpolationType = InterpolationType.LastValue
        )
            .add(0, 5)
            .add(2,10)
            .add(3,5)

        val x = SingleValue(Name("x"), TypeInfo.of(Int::class.javaObjectType), 1)

        val constants = Node.Constants(
            Title("const"), SingleValues(
                listOf(x)
            )
        )

        val table = Node.Table(
            Title("table"), TypeInfo.of(Int::class.javaObjectType), Columns(listOf(a, b))
        )
        val destination = ColumnId()
        val formula = Node.Calculated(Title("calc"), TypeInfo.of(Int::class.javaObjectType), Calculations(TypeInfo.of(Int::class.javaObjectType),
            tabular = listOf(Calculation.Tabular(
                TypeInfo.of(Int::class.javaObjectType),
                Name("y"),
                EvalFormulaAdapter("x+b+c"),
                interpolationType = InterpolationType.Linear,
                destination = destination
            ))
        ).let { c ->
            c.connect(c.inputs()[0].id, Source.ColumnSource(table.id, a.id, TypeInfo.of(Int::class.javaObjectType)))
                .connect(c.inputs()[1].id, Source.ColumnSource(table.id, b.id, TypeInfo.of(Int::class.javaObjectType)))
                .connect(c.inputs()[2].id, Source.ValueSource(constants.id, x.id))
        })

        val source = Model(listOf(constants, table, formula))
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