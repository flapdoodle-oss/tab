package de.flapdoodle.tab.ui

import de.flapdoodle.tab.model.Tab2Model
import de.flapdoodle.tab.model.calculations.Calculation
import de.flapdoodle.tab.model.calculations.Calculations
import de.flapdoodle.tab.model.calculations.adapter.EvalFormulaAdapter
import de.flapdoodle.tab.model.calculations.interpolation.InterpolationType
import de.flapdoodle.tab.model.connections.Source
import de.flapdoodle.tab.model.data.Column
import de.flapdoodle.tab.model.data.ColumnId
import de.flapdoodle.tab.model.data.Columns
import de.flapdoodle.tab.ui.events.ModelEvent
import de.flapdoodle.types.Either
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigInteger

class ModelSolverWrapperTest {
    @Test
    fun singleTableConnecton() {
        val x = Column("x", Int::class, Int::class)
            .add(0, 1)
            .add(1,2)
            .add(3,10)

        val y = Column("y", Int::class, Int::class)
            .add(0, 2)
            .add(1,4)
            .add(3,20)

        val table = de.flapdoodle.tab.model.Node.Table(
            "table", Int::class, Columns(listOf(x, y))
        )
        val destination = ColumnId()
        val formula = de.flapdoodle.tab.model.Node.Calculated("calc", Int::class, Calculations(Int::class,
            tabular = listOf(Calculation.Tabular(Int::class,"y", EvalFormulaAdapter("x+2"), InterpolationType.Linear, destination))
        ).let { c -> c.connect(c.inputs()[0].id, Source.ColumnSource(table.id, x.id, Int::class)) })

        val source = Tab2Model(listOf(table, formula))
        val wrapper = ModelSolverWrapper(source)
//        val changed = Solver.solve(source)

        val data = wrapper.model().value.node(formula.id).column(destination)
        assertThat(data.values)
            .isEqualTo(mapOf(
                0 to BigInteger.valueOf(3),
                1 to BigInteger.valueOf(4),
                3 to BigInteger.valueOf(12)
            ))

        wrapper.eventListener().onEvent(
            ModelEvent.ConnectTo(
            table.id,
            Either.left(y.id),
            formula.id,
            Either.right(formula.calculations.inputs()[0].id),
        ))

        val afterChange = wrapper.model().value.node(formula.id).column(destination)
        assertThat(afterChange.values)
            .isEqualTo(mapOf(
                0 to BigInteger.valueOf(4),
                1 to BigInteger.valueOf(6),
                3 to BigInteger.valueOf(22)
            ))

//        source.connect(formula.id, formula.calculations.inputs()[0].id, table.id, Source.ColumnSource(table.id, y.id))

//        wrapper.changeModel {  }
    }

}