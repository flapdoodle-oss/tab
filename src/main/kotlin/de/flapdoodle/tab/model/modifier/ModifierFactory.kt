package de.flapdoodle.tab.model.modifier

import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.changes.Change
import de.flapdoodle.tab.types.one
import de.flapdoodle.tab.types.oneOrNull

object ModifierFactory {
    fun changes(nodes: List<Node>, change: Change): List<Modifier> {
        return when (change) {
            is Change.AddNode -> listOf(AddNode(change.node))
            is Change.RemoveNode -> Disconnect.removeSource(nodes, change.id) + RemoveNode(change.id)
            is Change.Move -> listOf(Move(change.id, change.position))
            is Change.Resize -> listOf(Resize(change.id, change.position, change.size))
            is Change.Connect -> listOf(Connect.map(nodes, change))
            is Change.Disconnect -> listOf(Disconnect.removeConnection(nodes, change.endId, change.input, change.source))
            is Change.Constants -> constantChanges(nodes, change)
            is Change.Table -> tableChanges(nodes, change)
            is Change.Calculation -> calculationChanges(nodes, change)
            else -> throw IllegalArgumentException("not implemented: $change")
        }
    }

    // VisibleForTesting
    internal fun constantChanges(nodes: List<Node>, change: Change.Constants): List<Modifier> {
        return when (change) {
            is Change.Constants.Properties -> listOf(ConstantProperties(change.id, change.name))
            is Change.Constants.AddValue -> listOf(AddValue(change.id, change.value))
            is Change.Constants.ChangeValue -> listOf(ChangeValue(change.id, change.valueId, change.value))
            is Change.Constants.ValueProperties -> listOf(ValueProperties(change.id, change.valueId, change.name, change.color))
            is Change.Constants.RemoveValue -> Disconnect.removeSource(nodes, change.id, change.valueId) + RemoveValue(change.id, change.valueId)
            else -> throw IllegalArgumentException("not implemented: $change")
        }
    }

    // VisibleForTesting
    internal fun tableChanges(nodes: List<Node>, change: Change.Table): List<Modifier> {
        return when (change) {
            is Change.Table.Properties -> listOf(TableProperties(change.id, change.name))
            is Change.Table.AddColumn<out Comparable<*>> -> listOf(AddColumn(change.id, change.column))
            is Change.Table.ColumnProperties -> listOf(ColumnProperties(change.id, change.columnId, change.name, change.color, change.interpolationType))
            is Change.Table.SetColumns<out Comparable<*>> -> listOf(SetColumnValues.asModifier(change))
            is Change.Table.MoveValues<out Comparable<*>>  -> listOf(MoveColumnValues.asModifier(change))
            is Change.Table.RemoveValues<out Comparable<*>> -> listOf(RemoveColumnValues.asModifier(change))
            is Change.Table.RemoveColumn -> Disconnect.removeSource(nodes, change.id, change.columnId)  + RemoveColumn(change.id, change.columnId)
            else -> throw IllegalArgumentException("not implemented: $change")
        }
    }

    internal fun calculationChanges(nodes: List<Node>, change: Change.Calculation): List<Modifier> {
        return when (change) {
            is Change.Calculation.Properties -> listOf(CalculationProperties(change.id, change.name))
            is Change.Calculation.AddAggregation -> listOf(AddAggregation(change.id, change.name, change.expression))
            is Change.Calculation.AddTabular -> listOf(AddTabular(change.id, change.name, change.expression, change.color, change.interpolationType))
            is Change.Calculation.ChangeAggregation -> listOf(ChangeAggregation(change.id,change.calculationId, change.name, change.formula))
            is Change.Calculation.ChangeTabular -> listOf(ChangeTabular(change.id,change.calculationId, change.name, change.formula, change.color, change.interpolationType))
            is Change.Calculation.ChangeFormula -> changeFormula(nodes, change)
            is Change.Calculation.RemoveFormula -> Disconnect.removeCalculation(nodes, change.id, change.calculationId) + RemoveFormula(change.id, change.calculationId)
            else -> throw IllegalArgumentException("not implemented: $change")
        }
    }

    private fun changeFormula(nodes: List<Node>, change: Change.Calculation.ChangeFormula): List<Modifier> {
        val calculated = nodes.one { it.id == change.id } as Node.Calculated<out Comparable<*>>
        val aggregation = calculated.calculations.aggregations().oneOrNull { it.id == change.calculationId }
        val tabular = calculated.calculations.tabular().oneOrNull { it.id == change.calculationId }

        return listOf(if (aggregation != null) {
            ChangeAggregation(change.id, change.calculationId, aggregation.name(), change.formula)
        } else {
            requireNotNull(tabular) {"calculation ${change.calculationId} not found in $calculated"}
            ChangeTabular(change.id, change.calculationId, tabular.name(), change.formula, tabular.color(), tabular.interpolationType())
        })
    }
}