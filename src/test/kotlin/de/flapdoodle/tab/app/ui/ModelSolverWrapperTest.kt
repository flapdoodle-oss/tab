package de.flapdoodle.tab.app.ui

import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.Tab2Model
import de.flapdoodle.tab.app.model.calculations.Calculation
import de.flapdoodle.tab.app.model.calculations.Calculations
import de.flapdoodle.tab.app.model.calculations.EvalAdapter
import de.flapdoodle.tab.app.model.connections.Source
import de.flapdoodle.tab.app.model.data.Column
import de.flapdoodle.tab.app.model.data.ColumnId
import de.flapdoodle.tab.app.model.data.Columns
import de.flapdoodle.tab.app.model.graph.Solver
import de.flapdoodle.tab.app.ui.events.ModelEvent
import de.flapdoodle.types.Either
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

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

        val table = Node.Table(
            "table", Int::class, Columns(listOf(x, y))
        )
        val destination = ColumnId(Int::class)
        val formula = Node.Calculated("calc", Int::class, Calculations(
            tabular = listOf(Calculation.Tabular("y", EvalAdapter("x+2"), destination))
        ).let { c -> c.connect(c.inputs()[0].id, Source.ColumnSource(table.id, x.id)) })

        val source = Tab2Model(listOf(table, formula))
        val wrapper = ModelSolverWrapper(source)
//        val changed = Solver.solve(source)

        val data = wrapper.model().value.node(formula.id).column(destination)
        assertThat(data.values)
            .isEqualTo(mapOf(
                0 to BigDecimal.valueOf(3),
                1 to BigDecimal.valueOf(4),
                3 to BigDecimal.valueOf(12)
            ))

        wrapper.eventListener().onEvent(ModelEvent.ConnectTo(
            table.id,
            Either.left(y.id),
            formula.id,
            Either.right(formula.calculations.inputs()[0].id),
        ))

        val afterChange = wrapper.model().value.node(formula.id).column(destination)
        assertThat(afterChange.values)
            .isEqualTo(mapOf(
                0 to BigDecimal.valueOf(4),
                1 to BigDecimal.valueOf(6),
                3 to BigDecimal.valueOf(22)
            ))

//        source.connect(formula.id, formula.calculations.inputs()[0].id, table.id, Source.ColumnSource(table.id, y.id))

//        wrapper.changeModel {  }
    }

}