package de.flapdoodle.tab.app.model

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.calculations.InputSlot
import de.flapdoodle.tab.app.model.connections.Source
import de.flapdoodle.tab.app.model.data.ColumnId
import de.flapdoodle.tab.app.model.data.DataId
import de.flapdoodle.tab.app.model.data.SingleValueId
import de.flapdoodle.tab.types.Change
import de.flapdoodle.tab.types.change
import de.flapdoodle.tab.types.one
import de.flapdoodle.types.Either

data class Tab2Model(
    val nodes: List<Node> = emptyList()
) {
    private val nodesById = nodes.associateBy { it.id }
    init {
        val collisions = nodes.groupBy { it.id }.filter { it.value.size>1 }
        require(collisions.isEmpty()) { "node id collisions: ${collisions.keys}"}
    }

    fun addNode(node: Node): Tab2Model {
        return copy(nodes = nodes + node)
    }

    fun removeNode(id: Id<out Node>): Tab2Model {
        val node = node(id)
        val withoutNode = nodes.filter { it.id != node.id }
        val cleaned = withoutNode.map { it.removeConnectionsFrom(id) }
        return copy(nodes = cleaned)
    }

    fun node(id: Id<out Node>): Node {
        return requireNotNull(nodesById[id]) { "could not find node: $id"}
    }

    fun node(id: Id<out Node.Calculated<*>>): Node.Calculated<*> {
        return nodes.one { it.id == id } as Node.Calculated<*>
    }

    fun connect(
        startId: Id<out Node>,
        startDataOrInput: Either<DataId, Id<InputSlot<*>>>,
        endId: Id<out Node>,
        endDataOrInput: Either<DataId, Id<InputSlot<*>>>
    ): Tab2Model {
        require(startDataOrInput.isLeft != endDataOrInput.isLeft) {"can not connect input to input/output to output"}
        require(startId!=endId) {"can not connect to itself"}

        val start: Node
        val end: Node
        val dataId: DataId
        val inputId: Id<InputSlot<*>>

        if (startDataOrInput.isLeft) {
            // dataId 2 input
            start=node(startId)
            end = node(endId)
            dataId = startDataOrInput.left()
            inputId = endDataOrInput.right()
        } else {
            // input 2 dataId
            start=node(endId)
            end = node(startId)
            dataId = endDataOrInput.left()
            inputId = startDataOrInput.right()
        }

        require(end is Node.Calculated<out Comparable<*>>) {"wrong node type: $end"}
        val connected = end.connect(inputId, when (dataId) {
            is ColumnId<out Comparable<*>> -> Source.ColumnSource(startId, dataId)
            is SingleValueId -> Source.ValueSource(startId, dataId)
        })

        return copy(nodes = nodes.change(Node::id, connected.id) { connected })
    }


    companion object {
        fun nodeChanges(old: Tab2Model, current: Tab2Model): Change<Node> {
            return Change.diff(old.nodes, current.nodes, Node::id)
        }

        fun connectionChanges(old: Tab2Model, current: Tab2Model): Change<Pair<Source, Pair<Id<Node.Calculated<*>>, InputSlot<out Comparable<*>>>>> {
            val oldInputs = nodeAndInputs(old.nodes)
            val currentInputs = nodeAndInputs(current.nodes)
            return Change.diff(oldInputs, currentInputs) { it.first to (it.second.first to it.second.second.id) }
        }

        private fun nodeAndInputs(nodes: List<Node>): List<Pair<Source, Pair<Id<Node.Calculated<*>>, InputSlot<out Comparable<*>>>>> {
            return nodes.filterIsInstance<Node.Calculated<out Comparable<*>>>().flatMap { node ->
                node.calculations.inputs().map { node to it }
            }.filter {
                it.second.source!=null
            }.map {
                it.second.source!! to (it.first.id to it.second)
            }
        }
    }
}