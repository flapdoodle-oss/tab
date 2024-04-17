package de.flapdoodle.tab.app.io.mapper

import de.flapdoodle.tab.app.io.adapter.ToFileMapping
import de.flapdoodle.tab.app.io.adapter.ToModelMapping
import de.flapdoodle.tab.app.io.file.FileNode
import de.flapdoodle.tab.app.io.file.Tab2File
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.Tab2Model

class DefaultModelFileMapper(
    private val nodeMapper: Mapper<Node, FileNode> = DefaultNodeMapper()
) : Mapper<Tab2Model, Tab2File> {

    override fun toFile(toFileMapping: ToFileMapping, src: Tab2Model): Tab2File {
        return Tab2File(src.nodes.map { nodeMapper.toFile(toFileMapping, it) })
    }

    override fun toModel(toModelMapping: ToModelMapping, src: Tab2File): Tab2Model {
        return Tab2Model(src.nodes.map { nodeMapper.toModel(toModelMapping, it) })
    }
}