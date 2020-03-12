package de.flapdoodle.tab.graph.nodes.renderer.graph

import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.NamedColumn
import de.flapdoodle.tab.data.TabModel
import de.flapdoodle.tab.data.calculations.CalculationMapping
import de.flapdoodle.tab.data.calculations.Calculations
import de.flapdoodle.tab.data.calculations.EvalExCalculationAdapter
import de.flapdoodle.tab.data.graph.ColumnGraph
import de.flapdoodle.tab.data.nodes.ColumnConnection
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.values.Input
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class ColumnGraphTest {

  @Test
  fun `give columns to which we can connect`() {
    val fooColumnId = ColumnId.create<String>()
    val barColumnId = ColumnId.create<Int>()
    val numberColumnId = ColumnId.create<BigDecimal>()

    val source = ConnectableNode.Table("source")
        .add(fooColumnId, "foo")
        .add(barColumnId, "bar")
        .add(numberColumnId, "numbers")

    val stringOpSample = ConnectableNode.Calculated("string op",
        calculations = listOf(CalculationMapping(
            calculation = Calculations.Calc_1(
                a = Input.Variable(String::class, "name"),
                formula = { s -> ">$s<" }
            ),
            column = NamedColumn("nameCol", ColumnId.create())
        )))

    val numberOpSample = ConnectableNode.Calculated("add 10",
        calculations = listOf(CalculationMapping(
            calculation = Calculations.Calc_1(
                a = Input.Variable(Int::class, "x"),
                formula = { s -> s?.let { it + 10 } }
            ),
            column = NamedColumn("offset", ColumnId.create())
        ))
    )

    val otherNumSample = ConnectableNode.Calculated("formula",
        calculations = listOf(CalculationMapping(
            calculation = EvalExCalculationAdapter("a*10"),
            column = NamedColumn("result", ColumnId.create())
        ))
    )

    val model = TabModel().add(source)
        .add(stringOpSample)
        .add(numberOpSample)
        .add(otherNumSample)
        .connect(stringOpSample.id, Input.Variable(String::class, "name"), ColumnConnection.ColumnValues(fooColumnId))
        .connect(numberOpSample.id, Input.Variable(Int::class, "x"), ColumnConnection.ColumnValues(barColumnId))
        .connect(otherNumSample.id, Input.Variable(BigDecimal::class, "a"), ColumnConnection.ColumnValues(numberColumnId))

    val graph = ColumnGraph.of(model.nodes, model.nodeConnections)

    val destsForCalculatedColumn = graph.possibleDestinationsFor(otherNumSample.calculations()[0].column.id)

    assertThat(destsForCalculatedColumn)
        .containsExactlyInAnyOrderElementsOf(source.columns().map { it.id } +
            stringOpSample.columns().map { it.id } +
            numberOpSample.columns().map { it.id } - numberColumnId
        )

    val destsForNumberColumn = graph.possibleDestinationsFor(numberColumnId)

    assertThat(destsForNumberColumn)
        .containsExactlyInAnyOrderElementsOf(source.columns().map { it.id } +
            stringOpSample.columns().map { it.id } +
            numberOpSample.columns().map { it.id } +
            otherNumSample.columns().map { it.id } - numberColumnId
        )

    val sourcesForCalculatedColumn = graph.possibleSourcesFor(otherNumSample.calculations()[0].column.id)

    assertThat(sourcesForCalculatedColumn)
        .containsExactlyInAnyOrderElementsOf(source.columns().map { it.id } +
            stringOpSample.columns().map { it.id } +
            numberOpSample.columns().map { it.id }
        )

    val sourcesForNumberColumn = graph.possibleSourcesFor(numberColumnId)

    assertThat(sourcesForNumberColumn)
        .containsExactlyInAnyOrderElementsOf(source.columns().map { it.id } +
            stringOpSample.columns().map { it.id } +
            numberOpSample.columns().map { it.id } - numberColumnId)

  }
}