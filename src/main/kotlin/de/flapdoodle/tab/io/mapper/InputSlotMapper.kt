package de.flapdoodle.tab.io.mapper

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.io.adapter.ToFileMapping
import de.flapdoodle.tab.io.adapter.ToModelMapping
import de.flapdoodle.tab.io.file.FileInputSlot
import de.flapdoodle.tab.model.calculations.InputSlot
import kotlin.reflect.KClass

interface InputSlotMapper {
    fun <K: Comparable<K>> toFile(toFileMapping: ToFileMapping, src: InputSlot<K>): FileInputSlot
    fun <K: Comparable<K>> toModel(toModelMapping: ToModelMapping, indexType: TypeInfo<K>, src: FileInputSlot): InputSlot<K>
}