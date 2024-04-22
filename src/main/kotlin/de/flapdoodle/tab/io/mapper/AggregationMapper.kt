package de.flapdoodle.tab.io.mapper

import de.flapdoodle.tab.io.adapter.ToFileMapping
import de.flapdoodle.tab.io.adapter.ToModelMapping
import de.flapdoodle.tab.io.file.FileCalculation
import de.flapdoodle.tab.model.calculations.Calculation
import kotlin.reflect.KClass

interface AggregationMapper {
    fun <K: Comparable<K>> toFile(toFileMapping: ToFileMapping, src: Calculation.Aggregation<K>): FileCalculation
    fun <K: Comparable<K>> toModel(toModelMapping: ToModelMapping, indexType: KClass<K>, src: FileCalculation): Calculation.Aggregation<K>
}