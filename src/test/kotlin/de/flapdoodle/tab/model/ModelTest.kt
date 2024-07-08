package de.flapdoodle.tab.model

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.calculations.Calculation
import de.flapdoodle.tab.model.calculations.Calculations
import de.flapdoodle.tab.model.calculations.adapter.EvalFormulaAdapter
import de.flapdoodle.tab.model.calculations.interpolation.InterpolationType
import de.flapdoodle.tab.model.changes.Change
import de.flapdoodle.tab.model.connections.Source
import de.flapdoodle.tab.model.data.Column
import de.flapdoodle.tab.model.data.ColumnId
import de.flapdoodle.tab.model.data.Columns
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

class ModelTest {
    @Test
    fun addAndRemoveNode() {
        val a = randomNode()

        val withNode = Model()
            .apply(Change.AddNode(randomNode()))
            .apply(Change.AddNode(a))
            .apply(Change.AddNode(randomNode()))

        assertThat(withNode.nodes())
            .hasSize(3)
            .contains(a)

        val removed = withNode.apply(Change.RemoveNode(a.id))

        assertThat(removed.nodes())
            .hasSize(2)
            .doesNotContain(a)
    }

    @Test
    fun removingSourceColumnForExistingConnectionMustRemoveConnectionAsWell() {
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
            tabular = listOf(Calculation.Tabular(
                TypeInfo.of(Int::class.javaObjectType),
                Name("y"),
                EvalFormulaAdapter("x+2"),
                interpolationType = InterpolationType.Linear,
                destination = destination
            ))
        ).let { c -> c.connect(c.inputs()[0].id, Source.ColumnSource(table.id, x.id, TypeInfo.of(Int::class.javaObjectType))) })

        val testee = Model(listOf(table, formula))

        val withoutColumn = testee.apply(Change.Table.RemoveColumn(table.id, x.id))

        assertThat(withoutColumn.node(formula.id).calculations.inputs()[0].source)
            .isNull()
    }


    private fun randomNode(): Node {
        val node = Node.Constants(Title(UUID.randomUUID().toString()))
        return node
    }
}