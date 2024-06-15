package de.flapdoodle.tab.io.mapper

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.io.adapter.ToFileMapping
import de.flapdoodle.tab.io.adapter.ToModelMapping
import de.flapdoodle.tab.io.file.FileColor
import de.flapdoodle.tab.io.file.FileInputSlot
import de.flapdoodle.tab.io.file.FileVariable
import de.flapdoodle.tab.model.calculations.InputSlot
import de.flapdoodle.tab.model.calculations.Variable

class DefaultInputSlotMapper(
    private val sourceMapper: SourceMapper = DefaultSourceMapper(),
) : InputSlotMapper {
    override fun <K : Comparable<K>> toFile(toFileMapping: ToFileMapping, src: InputSlot<K>): FileInputSlot {
        return FileInputSlot(
            name = src.name,
            mapTo = src.mapTo.map {
                FileVariable(it.name, toFileMapping.idFor(it.id))
            }.toSet(),
            source = if (src.source!=null) sourceMapper.toFile(toFileMapping, src.source) else null,
            id = toFileMapping.idFor(src.id),
            color = FileColor.from(src.color)
        )
    }

    override fun <K : Comparable<K>> toModel(
        toModelMapping: ToModelMapping,
        indexType: TypeInfo<K>,
        src: FileInputSlot
    ): InputSlot<K> {
        return InputSlot(
            name = src.name,
            mapTo = src.mapTo.map {
                Variable(it.name, toModelMapping.nextId(it.id, Variable::class))
            }.toSet(),
            source = if (src.source!=null) sourceMapper.toModel(toModelMapping, indexType, src.source) else null,
            id = toModelMapping.nextId(src.id, InputSlot::class),
            color = src.color.toColor()
        )
    }
}