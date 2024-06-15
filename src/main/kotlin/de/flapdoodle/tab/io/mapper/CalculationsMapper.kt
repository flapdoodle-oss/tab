package de.flapdoodle.tab.io.mapper

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.io.adapter.ToFileMapping
import de.flapdoodle.tab.io.adapter.ToModelMapping
import de.flapdoodle.tab.io.file.FileCalculations
import de.flapdoodle.tab.model.calculations.Calculations

interface CalculationsMapper {
    fun toFile(toFileMapping: ToFileMapping, src: Calculations<out Comparable<*>>): FileCalculations
    fun <K: Comparable<K>> toModel(toModelMapping: ToModelMapping, indexType: TypeInfo<K>, src: FileCalculations): Calculations<K>
}