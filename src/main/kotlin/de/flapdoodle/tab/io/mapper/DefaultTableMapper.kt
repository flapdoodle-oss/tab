package de.flapdoodle.tab.io.mapper

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.io.adapter.ToFileMapping
import de.flapdoodle.tab.io.adapter.ToModelMapping
import de.flapdoodle.tab.io.file.FileNode
import de.flapdoodle.tab.model.Title

class DefaultTableMapper(
    private val columnsMapper: ColumnsMapper = DefaultColumnsMapper()
) : Mapper<de.flapdoodle.tab.model.Node.Table<out Comparable<*>>, FileNode> {
    override fun toFile(toFileMapping: ToFileMapping, src: de.flapdoodle.tab.model.Node.Table<out Comparable<*>>): FileNode {
        return FileNode(
            name = src.name.long,
            short = src.name.short,
            description = src.name.description,
            position = src.position,
            size = src.size,
            id = toFileMapping.idFor(src.id),
            table = FileNode.Table(
                indexType = toFileMapping.indexType(src.indexType),
                columns = columnsMapper.toFile(toFileMapping, src.columns)
            )
        )
    }

    override fun toModel(toModelMapping: ToModelMapping, src: FileNode): de.flapdoodle.tab.model.Node.Table<Comparable<Any>> {
        requireNotNull(src.table) { "table is not set" }
        return toModel(toModelMapping, src, src.table, toModelMapping.indexType(src.table.indexType) as TypeInfo<Comparable<Any>>)
    }

    private fun <T: Comparable<T>> toModel(
        toModelMapping: ToModelMapping,
        src: FileNode,
        table: FileNode.Table,
        indexType: TypeInfo<T>
    ): de.flapdoodle.tab.model.Node.Table<T> {
        return de.flapdoodle.tab.model.Node.Table(
            name = Title(src.name, src.short, src.description),
            indexType = indexType,
            columns = columnsMapper.toModel(toModelMapping, indexType, table.columns),
            id = toModelMapping.nextId(src.id, de.flapdoodle.tab.model.Node.Table::class),
            position = src.position
        )
    }
}