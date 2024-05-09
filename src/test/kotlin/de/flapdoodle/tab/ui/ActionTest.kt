package de.flapdoodle.tab.ui

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.Tab2Model
import de.flapdoodle.tab.model.calculations.Calculation
import de.flapdoodle.tab.model.calculations.Calculations
import de.flapdoodle.tab.model.calculations.adapter.Eval
import de.flapdoodle.tab.model.calculations.adapter.EvalFormulaAdapter
import de.flapdoodle.tab.model.change.ModelChange
import de.flapdoodle.tab.model.connections.Source
import de.flapdoodle.tab.model.data.Column
import de.flapdoodle.tab.model.data.ColumnId
import de.flapdoodle.tab.model.data.Columns
import de.flapdoodle.types.Either
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.concurrent.ThreadLocalRandom

class ActionTest {

    @Test
    fun noChange() {
        val base = randomModel()

        assertThat(Action.syncActions(base, base))
            .isEmpty()
    }

    @Test
    fun changeNodes() {
        val base = randomModel()
        val node = Node.Constants("x")

        assertThat(Action.syncActions(base, base.addNode(node)))
            .containsExactly(
                Action.AddNode(node)
            )
        assertThat(Action.syncActions(base.addNode(node), base))
            .containsExactly(
                Action.RemoveNode(node.id)
            )
    }

    @Test
    fun changeColumn() {
        val base = randomModel()

        val tableId = Id.Companion.nextId(Node.Table::class)
        val columnId = ColumnId()

        val table = Node.Table(
            name = "table",
            indexType = Int::class,
            id = tableId
        )
        val tableWithColumn = table.copy(
            columns = Columns(
                columns = listOf(
                    Column(name = "x", indexType = Int::class, valueType = Int::class, id = columnId)
                )
            )
        )

        assertThat(Action.syncActions(base.addNode(table), base.addNode(tableWithColumn)))
            .containsExactly(
                Action.ChangeNode(
                    id = tableId,
                    node = tableWithColumn
                ),
                Action.AddOutput(
                    id = tableId,
                    output = tableWithColumn.column(columnId)
                )
            )

        assertThat(Action.syncActions(base.addNode(tableWithColumn), base.addNode(table)))
            .containsExactly(
                Action.RemoveOutput(
                    id = tableId,
                    output = columnId
                ),
                Action.ChangeNode(
                    id = tableId,
                    node = table
                )
            )
    }

    @Test
    fun changeConnections() {
        val base = randomModel()

        val tableId = Id.Companion.nextId(Node.Table::class)
        val columnId = ColumnId()
        val calculatedId = Id.nextId(Node.Calculated::class)

        val table = Node.Table(
            name = "table",
            indexType = Int::class,
            id = tableId,
            columns = Columns(
                columns = listOf(
                    Column(name = "x", indexType = Int::class, valueType = Int::class, id = columnId)
                )
            )
        )

        val calculated = Node.Calculated(
            name = "calculated",
            indexType = Int::class,
            id = calculatedId,
            calculations = Calculations(
                indexType = Int::class,
                tabular = listOf(
                    Calculation.Tabular(
                        indexType = Int::class,
                        name = "x",
                        formula = EvalFormulaAdapter("x")
                    )
                )
            )
        )

        val withTableAndCalculation = base.addNode(table).addNode(calculated)
        val inputSlotId = calculated.calculations.inputs()[0].id
        val connected = withTableAndCalculation.connect(tableId, Either.left(columnId), calculatedId, Either.right(
            inputSlotId
        ))

        assertThat(Action.syncActions(withTableAndCalculation, connected))
            .containsExactly(
                Action.ChangeNode(
                    id = calculatedId,
                    node = connected.node(calculatedId)
                ),
                Action.ChangeInput(
                    id = calculatedId,
                    input = inputSlotId,
                    change = connected.node(calculatedId).calculations.inputs()[0]
                ),
                Action.AddConnection(
                    source = connected.node(calculatedId).calculations.inputs()[0].source!!,
                    id = calculatedId,
                    input = inputSlotId
                )
            )

        assertThat(Action.syncActions(connected, withTableAndCalculation))
            .containsExactly(
                Action.RemoveConnection(
                    source = connected.node(calculatedId).calculations.inputs()[0].source!!,
                    id = calculatedId,
                    input = inputSlotId
                ),
                Action.ChangeNode(
                    id = calculatedId,
                    node = withTableAndCalculation.node(calculatedId)
                ),
                Action.ChangeInput(
                    id = calculatedId,
                    input = inputSlotId,
                    change = withTableAndCalculation.node(calculatedId).calculations.inputs()[0]
                )
            )
    }

    private fun randomModel(): Tab2Model {
        val random = ThreadLocalRandom.current()

        var model = emptyModel()
        if (random.nextBoolean()) {
            model = model.addNode(Node.Table("table#" + random.nextInt(), String::class))
        }
        if (random.nextBoolean()) {
            model = model.addNode(Node.Calculated("calculated#" + random.nextInt(), Double::class))
        }
        if (random.nextBoolean()) {
            model = model.addNode(Node.Constants("consts#" + random.nextInt()))
        }
        return model
    }

    @Test
    fun triggerConnectionChange() {
        val tableId = Id.Companion.nextId(Node.Table::class)
        val calculatedId = Id.nextId(Node.Calculated::class)

        val sourceModel = emptyModel()
            .addNode(
                Node.Table(
                    name = "source",
                    indexType = String::class,
                    id = tableId
                )
            )
            .addNode(
                Node.Calculated(
                    name = "calc",
                    indexType = String::class,
                    id = calculatedId
                )
            )
            .apply(
                ModelChange.AddColumn(
                    id = tableId,
                    column = Column(
                        name = "x",
                        indexType = String::class,
                        valueType = Double::class
                    )
                )
            )
            .apply(
                ModelChange.AddTabular(
                    id = calculatedId,
                    name = "y",
                    expression = "x"
                )
            )

        val columnX = (sourceModel.node(tableId) as Node.Table<String>).columns.columns()[0]
        val inputX = (sourceModel.node(calculatedId) as Node.Calculated<String>).calculations.inputs()[0]

        val connected = sourceModel.connect(tableId, Either.left(columnX.id), calculatedId, Either.right(inputX.id))

        val withSecondExpression = connected.apply(
            ModelChange.AddTabular(
                id = calculatedId,
                name = "z",
                expression = "x"
            )
        )

        val actions = Action.syncActions(connected, withSecondExpression)
        println("$actions")


    }

    private fun emptyModel() = Tab2Model()
}