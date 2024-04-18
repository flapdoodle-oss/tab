package de.flapdoodle.tab.app.io.mapper

import de.flapdoodle.tab.app.io.adapter.ToFileMapping
import de.flapdoodle.tab.app.io.adapter.ToModelMapping
import de.flapdoodle.tab.app.io.file.FileNode
import de.flapdoodle.tab.app.io.file.FileSingleValues
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.data.SingleValues

class DefaultConstantsMapper(
    private val singleValuesMapper: Mapper<SingleValues, FileSingleValues> = DefaultSingleValuesMapper()
) : Mapper<Node.Constants, FileNode> {
    override fun toFile(toFileMapping: ToFileMapping, src: Node.Constants): FileNode {
        return FileNode(
            src.name,
            src.position,
            toFileMapping.idFor(src.id),
            constants = FileNode.Constants(
                singleValuesMapper.toFile(toFileMapping, src.values)
            )
        )

    }

    override fun toModel(toModelMapping: ToModelMapping, src: FileNode): Node.Constants {
        require(src.constants != null) {"constants is not set"}
        
        return Node.Constants(
            name = src.name,
            position = src.position,
            id = toModelMapping.nextId(src.id, Node.Constants::class),
            values = singleValuesMapper.toModel(toModelMapping, src.constants.values)
        )
    }
}