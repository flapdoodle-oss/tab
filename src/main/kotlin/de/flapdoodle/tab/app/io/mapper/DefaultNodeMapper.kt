package de.flapdoodle.tab.app.io.mapper

import de.flapdoodle.tab.app.io.adapter.ToFileMapping
import de.flapdoodle.tab.app.io.adapter.ToModelMapping
import de.flapdoodle.tab.app.io.file.FileNode
import de.flapdoodle.tab.app.model.Node

class DefaultNodeMapper(
    private val constantsMapper: Mapper<Node.Constants, FileNode> = ConstantsMapper(),
    private val tableMapper: Mapper<Node.Table<out Comparable<*>>, FileNode> = DefaultTableMapper()
) : Mapper<Node, FileNode> {
    override fun toFile(toFileMapping: ToFileMapping, src: Node): FileNode {
        return when (src) {
            is Node.Constants -> {
                constantsMapper.toFile(toFileMapping, src)
            }
            is Node.Table<out Comparable<*>> -> {
                tableMapper.toFile(toFileMapping, src)
            }

            else -> TODO("Not yet implemented")
        }
    }

    override fun toModel(toModelMapping: ToModelMapping, src: FileNode): Node {
        if (src.constants!=null) {
            return constantsMapper.toModel(toModelMapping, src)
        }
        if (src.table!=null) {
            return tableMapper.toModel(toModelMapping, src)
        }
        TODO("Not yet implemented")
    }
}