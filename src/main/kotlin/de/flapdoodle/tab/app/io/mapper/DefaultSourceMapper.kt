package de.flapdoodle.tab.app.io.mapper

import de.flapdoodle.tab.app.io.adapter.ToFileMapping
import de.flapdoodle.tab.app.io.adapter.ToModelMapping
import de.flapdoodle.tab.app.io.file.FileDataId
import de.flapdoodle.tab.app.io.file.FileSource
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.connections.Source
import de.flapdoodle.tab.app.model.data.ColumnId
import de.flapdoodle.tab.app.model.data.DataId
import de.flapdoodle.tab.app.model.data.SingleValueId
import kotlin.reflect.KClass

class DefaultSourceMapper(
    private val dataIdMapper: Mapper<DataId, FileDataId> = DefaultDataIdMapper()
) : SourceMapper {
    override fun toFile(toFileMapping: ToFileMapping, src: Source): FileSource {
        return when (src) {
            is Source.ValueSource -> {
                FileSource(
                    node = toFileMapping.idFor(src.node),
                    nodeType = nodeType(src.node.type()),
                    id = toFileMapping.idFor(src.id),
                    dataId = dataIdMapper.toFile(toFileMapping, src.valueId)
                )
            }

            is Source.ColumnSource<out Comparable<*>> -> {
                FileSource(
                    node = toFileMapping.idFor(src.node),
                    nodeType = nodeType(src.node.type()),
                    id = toFileMapping.idFor(src.id),
                    dataId = dataIdMapper.toFile(toFileMapping, src.columnId)
                )
            }
        }
    }

    override fun <K : Comparable<K>> toModel(
        toModelMapping: ToModelMapping,
        indexType: KClass<K>,
        src: FileSource
    ): Source {
        return when (val dataId = dataIdMapper.toModel(toModelMapping, src.dataId)) {
            is SingleValueId -> {
                Source.ValueSource(
                    node = toModelMapping.nextId(src.node, nodeType(src.nodeType)),
                    valueId = dataId,
                    id = toModelMapping.nextId(src.id, Source.ValueSource::class)
                )
            }

            is ColumnId -> {
                Source.ColumnSource(
                    node = toModelMapping.nextId(src.node, nodeType(src.nodeType)),
                    columnId = dataId,
                    indexType = indexType,
                    id = toModelMapping.nextId(src.id, Source.ColumnSource::class)
                )
            }
        }
    }

    private fun nodeType(type: KClass<out Node>): String {
        return when (type) {
            Node.Calculated::class -> "Calculated"
            Node.Constants::class -> "Constants"
            Node.Table::class -> "Table"
            else -> {
                throw IllegalArgumentException("not implemented: $type")
            }
        }
    }

    private fun nodeType(nodeType: String): KClass<out Node> {
        return when (nodeType) {
            "Calculated" -> Node.Calculated::class
            "Constants" -> Node.Constants::class
            "Table" -> Node.Table::class
            else -> throw IllegalArgumentException("not implemented: $nodeType")
        }
    }
}