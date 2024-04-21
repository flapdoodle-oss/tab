package de.flapdoodle.tab.app.io.mapper

import de.flapdoodle.tab.app.io.adapter.ToFileMapping
import de.flapdoodle.tab.app.io.adapter.ToModelMapping
import de.flapdoodle.tab.app.io.file.FileInputSlot
import de.flapdoodle.tab.app.model.calculations.InputSlot
import kotlin.reflect.KClass

interface InputSlotMapper {
    fun <K: Comparable<K>> toFile(toFileMapping: ToFileMapping, src: InputSlot<K>): FileInputSlot
    fun <K: Comparable<K>> toModel(toModelMapping: ToModelMapping, indexType: KClass<K>, src: FileInputSlot): InputSlot<K>
}