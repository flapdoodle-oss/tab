package de.flapdoodle.tab.io.mapper

import de.flapdoodle.tab.io.adapter.ToFileMapping
import de.flapdoodle.tab.io.adapter.ToModelMapping
import de.flapdoodle.tab.io.file.FileNode
import kotlin.reflect.KClass

class DefaultTableMapper(
    private val columnsMapper: ColumnsMapper = DefaultColumnsMapper()
) : Mapper<de.flapdoodle.tab.model.Node.Table<out Comparable<*>>, FileNode> {
    override fun toFile(toFileMapping: ToFileMapping, src: de.flapdoodle.tab.model.Node.Table<out Comparable<*>>): FileNode {
        return FileNode(
            name = src.name,
            position = src.position,
            id = toFileMapping.idFor(src.id),
            table = FileNode.Table(
                indexType = toFileMapping.indexType(src.indexType),
                columns = columnsMapper.toFile(toFileMapping, src.columns)
            )
        )
    }

    override fun toModel(toModelMapping: ToModelMapping, src: FileNode): de.flapdoodle.tab.model.Node.Table<Comparable<Any>> {
        requireNotNull(src.table) { "table is not set" }
        return toModel(toModelMapping, src, src.table, toModelMapping.indexType(src.table.indexType) as KClass<Comparable<Any>>)
    }

    private fun <T: Comparable<T>> toModel(
        toModelMapping: ToModelMapping,
        src: FileNode,
        table: FileNode.Table,
        indexType: KClass<T>
    ): de.flapdoodle.tab.model.Node.Table<T> {
        return de.flapdoodle.tab.model.Node.Table(
            name = src.name,
            indexType = indexType,
            columns = columnsMapper.toModel(toModelMapping, indexType, table.columns),
            id = toModelMapping.nextId(src.id, de.flapdoodle.tab.model.Node.Table::class),
            position = src.position
        )
    }
}