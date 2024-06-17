package de.flapdoodle.tab.ui

import de.flapdoodle.kfx.types.Id
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
import de.flapdoodle.tab.model.data.Column
import de.flapdoodle.tab.model.data.ColumnId
import de.flapdoodle.tab.model.data.Columns
import de.flapdoodle.types.Either
import javafx.scene.paint.Color
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
        val node = Node.Constants(
            Title("x")
        )

        assertThat(Action.syncActions(base, base.apply(Change.AddNode(node))))
            .containsExactly(
                Action.AddNode(node)
            )
        assertThat(Action.syncActions(base.apply(Change.AddNode(node)), base))
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
            name = Title("table"),
            indexType = TypeInfo.of(Int::class.javaObjectType),
            id = tableId
        )
        val tableWithColumn = table.copy(
            columns = Columns(
                columns = listOf(
                    Column(
                        name = Name("x"),
                        indexType = TypeInfo.of(Int::class.javaObjectType),
                        valueType = TypeInfo.of(Int::class.javaObjectType),
                        id = columnId
                    )
                )
            )
        )

        assertThat(Action.syncActions(base.apply(Change.AddNode(table)), base.apply(Change.AddNode(tableWithColumn))))
            .containsExactly(
                Action.ChangeNode(
                    id = tableId,
                    node = tableWithColumn
                ),
                Action.AddOutput(
                    id = tableId,
                    output = tableWithColumn.column(columnId),
                    null
                )
            )

        assertThat(Action.syncActions(base.apply(Change.AddNode(tableWithColumn)), base.apply(Change.AddNode(table))))
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
            name = Title("table"),
            indexType = TypeInfo.of(Int::class.javaObjectType),
            id = tableId,
            columns = Columns(
                columns = listOf(
                    Column(
                        name = Name("x"),
                        indexType = TypeInfo.of(Int::class.javaObjectType),
                        valueType = TypeInfo.of(Int::class.javaObjectType),
                        id = columnId
                    )
                )
            )
        )

        val calculated = Node.Calculated(
            name = Title("calculated"),
            indexType = TypeInfo.of(Int::class.javaObjectType),
            id = calculatedId,
            calculations = Calculations(
                indexType = TypeInfo.of(Int::class.javaObjectType),
                tabular = listOf(
                    Calculation.Tabular(
                        indexType = TypeInfo.of(Int::class.javaObjectType),
                        name = Name("x"),
                        formula = EvalFormulaAdapter("x")
                    )
                )
            )
        )

        val withTableAndCalculation = base.apply(Change.AddNode(table)).apply(Change.AddNode(calculated))
        val inputSlotId = calculated.calculations.inputs()[0].id
        val connected = withTableAndCalculation.apply(
            Change.Connect(tableId, Either.left(columnId), calculatedId, Either.right(
            inputSlotId
        )))

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

    private fun randomModel(): Model {
        val random = ThreadLocalRandom.current()

        var model = emptyModel()
        if (random.nextBoolean()) {
            model = model.apply(Change.AddNode(Node.Table(Title("table#" + random.nextInt()), TypeInfo.of(String::class.javaObjectType))))
        }
        if (random.nextBoolean()) {
            model = model.apply(
                Change.AddNode(Node.Calculated(
                Title("calculated#" + random.nextInt()),
                TypeInfo.of(Double::class.javaObjectType)
            )))
        }
        if (random.nextBoolean()) {
            model = model.apply(Change.AddNode(Node.Constants(Title("consts#" + random.nextInt()))))
        }
        return model
    }

    @Test
    fun triggerConnectionChange() {
        val tableId = Id.Companion.nextId(Node.Table::class)
        val calculatedId = Id.nextId(Node.Calculated::class)

        val sourceModel = emptyModel()
            .apply(
                Change.AddNode(
                Node.Table(
                    name = Title("source"),
                    indexType = TypeInfo.of(String::class.javaObjectType),
                    id = tableId
                )
            ))
            .apply(
                Change.AddNode(
                Node.Calculated(
                    name = Title("calc"),
                    indexType = TypeInfo.of(String::class.javaObjectType),
                    id = calculatedId
                )
            ))
            .apply(
                Change.Table.AddColumn(
                    id = tableId,
                    column = Column(
                        name = Name("x"),
                        indexType = TypeInfo.of(String::class.javaObjectType),
                        valueType = TypeInfo.of(Double::class.javaObjectType)
                    )
                )
            )
            .apply(
                Change.Calculation.AddTabular(
                    id = calculatedId,
                    name = Name("y"),
                    expression = "x",
                    color = javafx.scene.paint.Color.RED,
                    interpolationType = InterpolationType.LastValue
                )
            )

        val columnX = (sourceModel.node(tableId) as Node.Table<String>).columns.columns()[0]
        val inputX = (sourceModel.node(calculatedId) as Node.Calculated<String>).calculations.inputs()[0]

        val connected = sourceModel.apply(Change.Connect(tableId, Either.left(columnX.id), calculatedId, Either.right(inputX.id)))

        val withSecondExpression = connected.apply(
            Change.Calculation.AddTabular(
                id = calculatedId,
                name = Name("z"),
                expression = "x",
                color = Color.RED,
                interpolationType = InterpolationType.LastValue
            )
        )

        val actions = Action.syncActions(connected, withSecondExpression)
//        println("$actions")
        // TODO hier fehlt der Test
        assertThat(actions).isNotEmpty()
    }

    private fun emptyModel() = Model()
}

