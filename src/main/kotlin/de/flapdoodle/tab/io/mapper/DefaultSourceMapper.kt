package de.flapdoodle.tab.io.mapper

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.io.adapter.ToFileMapping
import de.flapdoodle.tab.io.adapter.ToModelMapping
import de.flapdoodle.tab.io.file.FileDataId
import de.flapdoodle.tab.io.file.FileSource
import de.flapdoodle.tab.model.connections.Source
import de.flapdoodle.tab.model.data.ColumnId
import de.flapdoodle.tab.model.data.DataId
import de.flapdoodle.tab.model.data.SingleValueId
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
        indexType: TypeInfo<K>,
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

    private fun nodeType(type: KClass<out de.flapdoodle.tab.model.Node>): String {
        return when (type) {
            de.flapdoodle.tab.model.Node.Calculated::class -> "Calculated"
            de.flapdoodle.tab.model.Node.Constants::class -> "Constants"
            de.flapdoodle.tab.model.Node.Table::class -> "Table"
            else -> {
                throw IllegalArgumentException("not implemented: $type")
            }
        }
    }

    private fun nodeType(nodeType: String): KClass<out de.flapdoodle.tab.model.Node> {
        return when (nodeType) {
            "Calculated" -> de.flapdoodle.tab.model.Node.Calculated::class
            "Constants" -> de.flapdoodle.tab.model.Node.Constants::class
            "Table" -> de.flapdoodle.tab.model.Node.Table::class
            else -> throw IllegalArgumentException("not implemented: $nodeType")
        }
    }
}