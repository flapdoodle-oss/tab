package de.flapdoodle.tab.io.mapper

import de.flapdoodle.tab.io.adapter.ToFileMapping
import de.flapdoodle.tab.io.adapter.ToModelMapping
import de.flapdoodle.tab.io.file.FileNode
import de.flapdoodle.tab.io.file.FileSingleValues
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.model.Title
import de.flapdoodle.tab.model.data.SingleValues

class DefaultConstantsMapper(
    private val singleValuesMapper: Mapper<SingleValues, FileSingleValues> = DefaultSingleValuesMapper()
) : Mapper<de.flapdoodle.tab.model.Node.Constants, FileNode> {
    override fun toFile(toFileMapping: ToFileMapping, src: de.flapdoodle.tab.model.Node.Constants): FileNode {
        return FileNode(
            name = src.name.long,
            short = src.name.short,
            description = src.name.description,
            position = src.position,
            size = src.size,
            id = toFileMapping.idFor(src.id),
            constants = FileNode.Constants(
                singleValuesMapper.toFile(toFileMapping, src.values)
            )
        )

    }

    override fun toModel(toModelMapping: ToModelMapping, src: FileNode): de.flapdoodle.tab.model.Node.Constants {
        require(src.constants != null) {"constants is not set"}
        
        return de.flapdoodle.tab.model.Node.Constants(
            name = Title(src.name, src.short, src.description),
            position = src.position,
            id = toModelMapping.nextId(src.id, de.flapdoodle.tab.model.Node.Constants::class),
            values = singleValuesMapper.toModel(toModelMapping, src.constants.values)
        )
    }
}