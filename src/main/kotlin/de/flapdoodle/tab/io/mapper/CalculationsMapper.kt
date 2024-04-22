package de.flapdoodle.tab.io.mapper

import de.flapdoodle.tab.io.adapter.ToFileMapping
import de.flapdoodle.tab.io.adapter.ToModelMapping
import de.flapdoodle.tab.io.file.FileCalculations
import de.flapdoodle.tab.model.calculations.Calculations
import kotlin.reflect.KClass

interface CalculationsMapper {
    fun toFile(toFileMapping: ToFileMapping, src: Calculations<out Comparable<*>>): FileCalculations
    fun <K: Comparable<K>> toModel(toModelMapping: ToModelMapping, indexType: KClass<K>, src: FileCalculations): Calculations<K>
}