package de.flapdoodle.tab.model.modifier

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.Title
import de.flapdoodle.tab.model.calculations.Calculation
import de.flapdoodle.tab.model.calculations.Calculations
import de.flapdoodle.tab.model.calculations.InputSlot
import de.flapdoodle.tab.model.calculations.Variable
import de.flapdoodle.tab.model.calculations.adapter.EvalFormulaAdapter
import de.flapdoodle.tab.model.connections.Source
import de.flapdoodle.tab.model.data.SingleValue
import de.flapdoodle.tab.model.data.SingleValues
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class RemoveConnectionTest {

    @Test
    fun noChangesIfNoConnections() {
        val modifier = RemoveConnection.removeSource(emptyList(), Id.nextId(Node::class))
        assertThat(modifier).isEmpty()
    }

    @Test
    fun modifierForEachConnection() {
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
            source = Source.ValueSource(
                node = constants.id,
                valueId = singleValue.id
            )
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

        val modifier = RemoveConnection.removeConnections(calculated, constants.id)
        
        assertThat(modifier)
            .hasSize(1)
            .allSatisfy {
                assertThat(it.nodeId).isEqualTo(calculated.id)
                assertThat(it.input).isEqualTo(inputSlot.id)
                assertThat(it.source.node).isEqualTo(constants.id)
                assertThat(it.source.dataId()).isEqualTo(singleValue.id)
            }

        val withModifications = modifier[0].modify(listOf(constants, calculated))

        assertThat(withModifications)
            .hasSize(2)

        assertThat(withModifications[1].id).isEqualTo(calculated.id)
        assertThat((withModifications[1] as Node.Calculated<*>).calculations.inputs())
            .hasSize(1)
            .allSatisfy {
                assertThat(it.source).isNull()
            }
    }
}