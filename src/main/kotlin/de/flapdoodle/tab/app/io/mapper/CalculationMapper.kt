package de.flapdoodle.tab.app.io.mapper

import de.flapdoodle.tab.app.io.adapter.ToFileMapping
import de.flapdoodle.tab.app.io.adapter.ToModelMapping
import de.flapdoodle.tab.app.io.file.FileCalculation
import de.flapdoodle.tab.app.model.calculations.Calculation
import kotlin.reflect.KClass

interface CalculationMapper {
    fun <K: Comparable<K>> toFile(toFileMapping: ToFileMapping, src: Calculation<K>): FileCalculation
    fun <K: Comparable<K>> toModel(toModelMapping: ToModelMapping, indexType: KClass<K>, src: FileCalculation): Calculation<K>
}