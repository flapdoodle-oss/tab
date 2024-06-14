package de.flapdoodle.tab.model

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.calculations.Calculation
import de.flapdoodle.tab.model.calculations.Calculations
import de.flapdoodle.tab.model.calculations.adapter.EvalFormulaAdapter
import de.flapdoodle.tab.model.calculations.interpolation.InterpolationType
import de.flapdoodle.tab.model.change.ModelChange
import de.flapdoodle.tab.model.connections.Source
import de.flapdoodle.tab.model.data.Column
import de.flapdoodle.tab.model.data.ColumnId
import de.flapdoodle.tab.model.data.Columns
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class Tab2ModelTest {

    @Test
    @Disabled // refactoring ahead
    fun removingSourceColumnForExistingConnectionMustFail() {
        val x = Column(Name("x"), TypeInfo.of(Int::class.javaObjectType), TypeInfo.of(Int::class.javaObjectType))
            .add(0, 1)
            .add(1,2)
            .add(3,10)

        val table = Node.Table(
            Title("table"), TypeInfo.of(Int::class.javaObjectType), Columns(listOf(x))
        )
        val destination = ColumnId()
        val formula = Node.Calculated(Title("calc"), TypeInfo.of(Int::class.javaObjectType), Calculations(
            TypeInfo.of(Int::class.javaObjectType),
            tabular = listOf(Calculation.Tabular(TypeInfo.of(Int::class.javaObjectType),Name("y"), EvalFormulaAdapter("x+2"), InterpolationType.Linear, destination))
        ).let { c -> c.connect(c.inputs()[0].id, Source.ColumnSource(table.id, x.id, TypeInfo.of(Int::class.javaObjectType))) })

        val testee = Tab2Model(listOf(table, formula))

        val withoutColumn = testee.apply(ModelChange.RemoveColumn(table.id, x.id))

        assertThat(withoutColumn.node(formula.id).calculations.inputs()[0].source)
            .isNull()
    }
}