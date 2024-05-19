package de.flapdoodle.tab.io.mapper

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.io.adapter.ToFileMapping
import de.flapdoodle.tab.io.adapter.ToModelMapping
import de.flapdoodle.tab.io.file.FileNode
import de.flapdoodle.tab.io.file.FileSingleValues
import de.flapdoodle.tab.model.data.SingleValues
import kotlin.reflect.KClass

class DefaultCalculatedMapper(
    private val calculationsMapper: CalculationsMapper = DefaultCalculationsMapper(),
    private val columnsMapper: ColumnsMapper = DefaultColumnsMapper(),
    private val valuesMapper: Mapper<SingleValues, FileSingleValues> = DefaultSingleValuesMapper()
) : Mapper<de.flapdoodle.tab.model.Node.Calculated<out Comparable<*>>, FileNode> {
    override fun toFile(toFileMapping: ToFileMapping, src: de.flapdoodle.tab.model.Node.Calculated<out Comparable<*>>): FileNode {
        return FileNode(
            name = src.name,
            position = src.position,
            size = src.size,
            id = toFileMapping.idFor(src.id),
            calculated = FileNode.Calculated(
                indexType = toFileMapping.indexType(src.indexType),
                calculations = calculationsMapper.toFile(toFileMapping, src.calculations),
                columns = columnsMapper.toFile(toFileMapping, src.columns),
                values = valuesMapper.toFile(toFileMapping, src.values)
            )
        )
    }

    override fun toModel(toModelMapping: ToModelMapping, src: FileNode): de.flapdoodle.tab.model.Node.Calculated<Comparable<Any>> {
        requireNotNull(src.calculated) { "calculated is not set" }
        return toModel(toModelMapping, src, src.calculated, toModelMapping.indexType(src.calculated.indexType) as TypeInfo<Comparable<Any>>)
    }

    private fun <T: Comparable<T>> toModel(
        toModelMapping: ToModelMapping,
        src: FileNode,
        calculated: FileNode.Calculated,
        indexType: TypeInfo<T>
    ): de.flapdoodle.tab.model.Node.Calculated<T> {
        return de.flapdoodle.tab.model.Node.Calculated(
            name = src.name,
            indexType = indexType,
            calculations = calculationsMapper.toModel(toModelMapping, indexType, calculated.calculations),
            columns = columnsMapper.toModel(toModelMapping, indexType, calculated.columns),
            values = valuesMapper.toModel(toModelMapping, calculated.values),
            id = toModelMapping.nextId(src.id, de.flapdoodle.tab.model.Node.Calculated::class),
            position = src.position
        )
    }
}