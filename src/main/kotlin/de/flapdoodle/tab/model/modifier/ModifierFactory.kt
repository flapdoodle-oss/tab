package de.flapdoodle.tab.model.modifier

import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.changes.Change

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
            else -> throw IllegalArgumentException("not implemented: $change")
        }
    }

    // VisibleForTesting
    internal fun constantChanges(nodes: List<Node>, change: Change.Constants): List<Modifier> {
        return when (change) {
            is Change.Constants.Properties -> listOf(ConstantProperties(change.id, change.name))
            is Change.Constants.AddValue -> listOf(AddValue(change.id, change.value))
            is Change.Constants.ChangeValue -> listOf(ChangeValue(change.id, change.valueId, change.value))
            is Change.Constants.ValueProperties -> listOf(ValueProperties(change.id, change.valueId, change.name))
            is Change.Constants.RemoveValue -> Disconnect.removeSource(nodes, change.id, change.valueId) + RemoveValue(change.id, change.valueId)
            else -> throw IllegalArgumentException("not implemented: $change")
        }
    }

    // VisibleForTesting
    internal fun tableChanges(nodes: List<Node>, change: Change.Table): List<Modifier> {
        return when (change) {
            is Change.Table.Properties -> listOf(TableProperties(change.id, change.name))
            is Change.Table.AddColumn<out Comparable<*>> -> listOf(AddColumn(change.id, change.column))
            is Change.Table.ColumnProperties -> listOf(ColumnProperties(change.id, change.columnId, change.name, change.interpolationType))
            is Change.Table.SetColumns<out Comparable<*>> -> listOf(SetColumnValues.asModifier(change))
            is Change.Table.MoveValues<out Comparable<*>>  -> listOf(MoveColumnValues.asModifier(change))
            is Change.Table.RemoveValues<out Comparable<*>> -> listOf(RemoveColumnValues.asModifier(change))
            is Change.Table.RemoveColumn -> Disconnect.removeSource(nodes, change.id, change.columnId)  + RemoveColumn(change.id, change.columnId)
            else -> throw IllegalArgumentException("not implemented: $change")
        }
    }
}