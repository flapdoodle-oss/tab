package de.flapdoodle.tab.io.mapper

import de.flapdoodle.tab.io.adapter.ToFileMapping
import de.flapdoodle.tab.io.adapter.ToModelMapping
import de.flapdoodle.tab.io.file.FileNode

class DefaultNodeMapper(
    private val constantsMapper: Mapper<de.flapdoodle.tab.model.Node.Constants, FileNode> = DefaultConstantsMapper(),
    private val tableMapper: Mapper<de.flapdoodle.tab.model.Node.Table<out Comparable<*>>, FileNode> = DefaultTableMapper(),
    private val calculatedMapper: Mapper<de.flapdoodle.tab.model.Node.Calculated<out Comparable<*>>, FileNode> = DefaultCalculatedMapper()
) : Mapper<de.flapdoodle.tab.model.Node, FileNode> {
    override fun toFile(toFileMapping: ToFileMapping, src: de.flapdoodle.tab.model.Node): FileNode {
        return when (src) {
            is de.flapdoodle.tab.model.Node.Constants -> {
                constantsMapper.toFile(toFileMapping, src)
            }
            is de.flapdoodle.tab.model.Node.Table<out Comparable<*>> -> {
                tableMapper.toFile(toFileMapping, src)
            }
            is de.flapdoodle.tab.model.Node.Calculated<out Comparable<*>> -> {
                calculatedMapper.toFile(toFileMapping, src)
            }
        }
    }

    override fun toModel(toModelMapping: ToModelMapping, src: FileNode): de.flapdoodle.tab.model.Node {
        if (src.constants!=null) {
            return constantsMapper.toModel(toModelMapping, src)
        }
        if (src.table!=null) {
            return tableMapper.toModel(toModelMapping, src)
        }
        if (src.calculated!=null) {
            return calculatedMapper.toModel(toModelMapping, src)
        }
        throw IllegalArgumentException("not supported: $src")
    }
}