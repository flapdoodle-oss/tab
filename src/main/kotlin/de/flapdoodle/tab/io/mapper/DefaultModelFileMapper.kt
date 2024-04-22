package de.flapdoodle.tab.io.mapper

import de.flapdoodle.tab.io.adapter.ToFileMapping
import de.flapdoodle.tab.io.adapter.ToModelMapping
import de.flapdoodle.tab.io.file.FileNode
import de.flapdoodle.tab.io.file.Tab2File
import de.flapdoodle.tab.model.Tab2Model

class DefaultModelFileMapper(
    private val nodeMapper: Mapper<de.flapdoodle.tab.model.Node, FileNode> = DefaultNodeMapper()
) : Mapper<Tab2Model, Tab2File> {

    override fun toFile(toFileMapping: ToFileMapping, src: Tab2Model): Tab2File {
        return Tab2File(src.nodes.map { nodeMapper.toFile(toFileMapping, it) })
    }

    override fun toModel(toModelMapping: ToModelMapping, src: Tab2File): Tab2Model {
        return Tab2Model(src.nodes.map { nodeMapper.toModel(toModelMapping, it) })
    }
}