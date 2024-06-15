package de.flapdoodle.tab.model.modifier

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.*
import de.flapdoodle.tab.model.calculations.Calculation
import de.flapdoodle.tab.model.calculations.Calculations
import de.flapdoodle.tab.model.calculations.InputSlot
import de.flapdoodle.tab.model.calculations.Variable
import de.flapdoodle.tab.model.calculations.adapter.EvalFormulaAdapter
import de.flapdoodle.tab.model.connections.Source
import de.flapdoodle.tab.model.data.SingleValue
import de.flapdoodle.tab.model.data.SingleValues
import de.flapdoodle.types.Either
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.InstanceOfAssertFactories
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

class ModifierFactoryTest {
    @Test
    fun addAndRemoveNode() {
        val node = randomNode()

        assertThat(changesFor(Change.AddNode(node)))
            .containsExactly(AddNode(node))
        assertThat(changesFor(Change.RemoveNode(node.id)))
            .containsExactly(RemoveNode(node.id))
    }

    @Test
    fun moveAndResize() {
        val node = randomNode()
        val position = Position(10.0, 20.0)
        val size = Size(3.0, 4.0)

        assertThat(changesFor(Change.Move(node.id, position)))
            .containsExactly(Move(node.id, position))
        assertThat(changesFor(Change.Resize(node.id, position, size)))
            .containsExactly(Resize(node.id, position, size))
    }

    @Test
    fun connectAndDisconnect() {
        val indexType = TypeInfo.of(Int::class.java)

        val singleValue = SingleValue(
            name = Name("y"),
            valueType = TypeInfo.of(BigDecimal::class.java)
        )

        val constants = Node.Constants(
            name = Title("const"),
            values = SingleValues(
                listOf(
                    singleValue
                )
            )
        )

        val inputSlot = InputSlot<Int>(
            name = "y",
            mapTo = setOf(Variable("y")),
        )

        val calculated = Node.Calculated<Int>(
            name = Title("calc"),
            indexType = indexType,
            calculations = Calculations(
                indexType = indexType,
                aggregations = listOf(
                    Calculation.Aggregation(
                        indexType = indexType,
                        name = Name("x"),
                        formula = EvalFormulaAdapter(
                            formula = "y"
                        )
                    )
                ),
                inputs = listOf(
                    inputSlot
                )
            )
        )

        val connectChanges = ModifierFactory.changes(listOf(constants, calculated), Change.Connect(
                startId = constants.id,
                startDataOrInput = Either.left(singleValue.id),
                endId = calculated.id,
                endDataOrInput = Either.right(inputSlot.id)
            )
        )

        assertThat(connectChanges)
            .hasSize(1)
            .element(0, InstanceOfAssertFactories.type(Connect::class.java))
            .satisfies({
                assertThat(it.start).isEqualTo(constants)
                assertThat(it.dataId).isEqualTo(singleValue.id)
                assertThat(it.end).isEqualTo(calculated)
                assertThat(it.inputId).isEqualTo(inputSlot.id)
            })

        val disconnectChanges = ModifierFactory.changes(listOf(constants, calculated), Change.Disconnect(
            endId = calculated.id,
            input = inputSlot.id,
            source = Source.ValueSource(constants.id, singleValue.id)
        ))

        assertThat(disconnectChanges)
            .hasSize(1)
            .element(0, InstanceOfAssertFactories.type(Disconnect::class.java))
            .satisfies({
                assertThat(it.nodeId).isEqualTo(calculated.id)
                assertThat(it.input).isEqualTo(inputSlot.id)
                assertThat(it.source.node).isEqualTo(constants.id)
                assertThat(it.source.dataId()).isEqualTo(singleValue.id)
            })
    }

    private fun changesFor(change: Change): List<Modifier> {
        return ModifierFactory.changes(emptyList(), change)
    }

    private fun randomNode(): Node {
        val node = Node.Constants(Title(UUID.randomUUID().toString()))
        return node
    }
}