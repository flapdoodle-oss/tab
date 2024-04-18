package de.flapdoodle.tab.app.io.mapper

import de.flapdoodle.tab.app.io.adapter.ToFileMapping
import de.flapdoodle.tab.app.io.adapter.ToModelMapping
import de.flapdoodle.tab.app.io.file.FileNode
import de.flapdoodle.tab.app.model.Node
import kotlin.reflect.KClass

class DefaultCalculatedMapper(
//    private val calculationMapper: CalculationMapper = DefaultCalculationMapper(),

    private val columnsMapper: ColumnsMapper = DefaultColumnsMapper()
) : Mapper<Node.Calculated<out Comparable<*>>, FileNode> {
    override fun toFile(toFileMapping: ToFileMapping, src: Node.Calculated<out Comparable<*>>): FileNode {
        return FileNode(
            name = src.name,
            position = src.position,
            id = toFileMapping.idFor(src.id),
//            calculated = c
//            table = FileNode.Table(
//                indexType = toFileMapping.indexType(src.indexType),
//                columns = columnsMapper.toFile(toFileMapping, src.columns)
//            )
        )
    }

    override fun toModel(toModelMapping: ToModelMapping, src: FileNode): Node.Calculated<Comparable<Any>> {
        requireNotNull(src.table) { "table is not set" }
        return toModel(toModelMapping, src, src.table, toModelMapping.indexType(src.table.indexType) as KClass<Comparable<Any>>)
    }

    private fun <T: Comparable<T>> toModel(
        toModelMapping: ToModelMapping,
        src: FileNode,
        table: FileNode.Table,
        indexType: KClass<T>
    ): Node.Calculated<T> {
        return Node.Calculated(
            name = src.name,
            indexType = indexType,
            columns = columnsMapper.toModel(toModelMapping, indexType, table.columns),
//            id = toModelMapping.nextId(src.id, Node.Table::class),
            position = src.position
        )
    }
}