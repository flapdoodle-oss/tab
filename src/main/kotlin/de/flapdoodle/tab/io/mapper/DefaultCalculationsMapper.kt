package de.flapdoodle.tab.io.mapper

import de.flapdoodle.tab.io.adapter.ToFileMapping
import de.flapdoodle.tab.io.adapter.ToModelMapping
import de.flapdoodle.tab.io.file.FileCalculations
import de.flapdoodle.tab.model.calculations.Calculations
import kotlin.reflect.KClass

class DefaultCalculationsMapper(
    private val aggregationMapper: AggregationMapper = DefaultAggregationMapper(),
    private val tabularMapper: TabularMapper = DefaultTabularMapper(),
    private val inputSlotMapper: InputSlotMapper = DefaultInputSlotMapper()
) : CalculationsMapper {

    override fun toFile(toFileMapping: ToFileMapping, src: Calculations<out Comparable<*>>): FileCalculations {
        return FileCalculations(
            aggregations = src.aggregations().map { aggregationMapper.toFile(toFileMapping, it) },
            tabular = src.tabular().map { tabularMapper.toFile(toFileMapping, it) },
            inputs = src.inputs().map { inputSlotMapper.toFile(toFileMapping, it) }
        )
    }

    override fun <K : Comparable<K>> toModel(
        toModelMapping: ToModelMapping,
        indexType: KClass<K>,
        src: FileCalculations
    ): Calculations<K> {
        return Calculations(
            indexType = indexType,
            aggregations = src.aggregations.map { aggregationMapper.toModel(toModelMapping, indexType, it) },
            tabular = src.tabular.map { tabularMapper.toModel(toModelMapping, indexType, it) },
            inputs = src.inputs.map { inputSlotMapper.toModel(toModelMapping, indexType, it) }
        )
    }
}