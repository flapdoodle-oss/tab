package de.flapdoodle.tab.io.mapper

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.io.adapter.ToFileMapping
import de.flapdoodle.tab.io.adapter.ToModelMapping
import de.flapdoodle.tab.io.file.FileCalculation
import de.flapdoodle.tab.model.calculations.Calculation
import kotlin.reflect.KClass

interface TabularMapper {
    fun <K: Comparable<K>> toFile(toFileMapping: ToFileMapping, src: Calculation.Tabular<K>): FileCalculation
    fun <K: Comparable<K>> toModel(toModelMapping: ToModelMapping, indexType: TypeInfo<K>, src: FileCalculation): Calculation.Tabular<K>
}