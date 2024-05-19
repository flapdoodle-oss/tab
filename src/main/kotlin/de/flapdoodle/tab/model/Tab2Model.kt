package de.flapdoodle.tab.model

import de.flapdoodle.kfx.collections.Change
import de.flapdoodle.kfx.collections.Diff
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.calculations.InputSlot
import de.flapdoodle.tab.model.change.ModelChange
import de.flapdoodle.tab.model.connections.Source
import de.flapdoodle.tab.model.data.ColumnId
import de.flapdoodle.tab.model.data.DataId
import de.flapdoodle.tab.model.data.SingleValueId
import de.flapdoodle.tab.types.change
import de.flapdoodle.tab.types.one
import de.flapdoodle.types.Either

data class Tab2Model(
    val nodes: List<Node> = emptyList()
) {
    private val nodesById = nodes.associateBy { it.id }

    init {
        val collisions = nodes.groupBy { it.id }.filter { it.value.size > 1 }
        require(collisions.isEmpty()) { "node id collisions: ${collisions.keys}" }
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
        return requireNotNull(nodesById[id]) { "could not find node: $id" }
    }

    fun node(id: Id<out Node.Calculated<*>>): Node.Calculated<*> {
        return nodes.one { it.id == id } as Node.Calculated<*>
    }

    fun node(id: Id<out Node.Table<*>>): Node.Table<*> {
        return nodes.one { it.id == id } as Node.Table<*>
    }

    fun node(id: Id<out Node.Constants>): Node.Constants {
        return nodes.one { it.id == id } as Node.Constants
    }

    fun moveTo(nodeId: Id<out Node>, position: Position): Tab2Model {
        val node = node(nodeId)
        return copy(nodes = nodes.change(Node::id, nodeId) { it.moveTo(position) })
    }

    fun resizeTo(nodeId: Id<out Node>, position: Position, size: Size): Tab2Model {
        val node = node(nodeId)
        return copy(nodes = nodes.change(Node::id, nodeId) { it.resizeTo(position, size) })
    }

    fun disconnect(endId: Id<out Node>, input: Id<InputSlot<*>>, source: Source): Tab2Model {
        val end = node(endId)
        val removed = end.removeConnectionFrom(input, source.node, source.id)
        return copy(nodes = nodes.change(Node::id, end.id) { removed })
    }

    fun connect(
        startId: Id<out Node>,
        startDataOrInput: Either<DataId, Id<InputSlot<*>>>,
        endId: Id<out Node>,
        endDataOrInput: Either<DataId, Id<InputSlot<*>>>
    ): Tab2Model {
        require(startDataOrInput.isLeft != endDataOrInput.isLeft) { "can not connect input to input/output to output" }
        require(startId != endId) { "can not connect to itself" }

        val start: Node
        val end: Node
        val dataId: DataId
        val inputId: Id<InputSlot<*>>

        if (startDataOrInput.isLeft) {
            // dataId 2 input
            start = node(startId)
            end = node(endId)
            dataId = startDataOrInput.left()
            inputId = endDataOrInput.right()
        } else {
            // input 2 dataId
            start = node(endId)
            end = node(startId)
            dataId = endDataOrInput.left()
            inputId = startDataOrInput.right()
        }

        require(end is Node.Calculated<out Comparable<*>>) { "wrong node type: $end" }
        return connect(start, dataId, end, inputId)
    }

    private fun <K: Comparable<K>> connect(
        start: Node,
        dataId: DataId,
        end: Node.Calculated<K>,
        inputId: Id<InputSlot<*>>
    ): Tab2Model {
        val source = when (dataId) {
            is ColumnId -> {
                require(start is Node.HasColumns<out Comparable<*>>) { "wrong source node type: $start" }
                require(start.indexType == end.indexType) { "index type mismatch: ${start.indexType} != ${end.indexType}" }
                val startWithIndexType = start as Node.HasColumns<K>
                Source.ColumnSource(start.id, dataId, startWithIndexType.indexType)
            }

            is SingleValueId -> Source.ValueSource(start.id, dataId)
        }

        val connected = end.connect(inputId, source)

        return copy(nodes = nodes.change(Node::id, connected.id) { connected })
    }

    fun apply(change: ModelChange): Tab2Model {
        println("change: $change")
        return copy(nodes = nodes.map { it.apply(change) })
    }


    companion object {
        fun nodeChanges(old: Tab2Model, current: Tab2Model): Change<Node> {
            return Diff.between(old.nodes, current.nodes, Node::id)
        }

        fun connectionChanges(
            old: Tab2Model,
            current: Tab2Model
        ): Change<SourceAndDestination> {
            val oldInputs = nodeAndInputs(old.nodes)
            val currentInputs = nodeAndInputs(current.nodes)
            return Diff.between(oldInputs, currentInputs) { it.source to (it.destination to it.inputSlot.id) }
        }

        private fun nodeAndInputsY(nodes: List<Node>): List<Pair<Source, Pair<Id<Node.Calculated<*>>, InputSlot<out Comparable<*>>>>> {
            return nodes.filterIsInstance<Node.Calculated<out Comparable<*>>>().flatMap { node ->
                node.calculations.inputs().map { node to it }
            }.filter {
                it.second.source != null
            }.map {
                it.second.source!! to (it.first.id to it.second)
            }
        }

        private fun nodeAndInputs(nodes: List<Node>): List<SourceAndDestination> {
            return nodes.filterIsInstance<Node.Calculated<out Comparable<*>>>().flatMap { node ->
                node.calculations.inputs().map { node to it }
            }.filter {
                it.second.source != null
            }.map {
                SourceAndDestination(it.second.source!! , it.first.id, it.second)
            }
        }

        data class SourceAndDestination(
            val source: Source,
            val destination: Id<Node.Calculated<*>>,
            val inputSlot: InputSlot<out Comparable<*>>
        )
    }
}