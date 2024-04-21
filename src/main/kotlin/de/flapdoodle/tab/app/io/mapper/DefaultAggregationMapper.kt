package de.flapdoodle.tab.app.io.mapper

import de.flapdoodle.tab.app.io.adapter.ToFileMapping
import de.flapdoodle.tab.app.io.adapter.ToModelMapping
import de.flapdoodle.tab.app.io.file.FileCalculation
import de.flapdoodle.tab.app.io.file.FileFormula
import de.flapdoodle.tab.app.model.calculations.Calculation
import de.flapdoodle.tab.app.model.calculations.Formula
import de.flapdoodle.tab.app.model.data.SingleValueId
import kotlin.reflect.KClass

class DefaultAggregationMapper(
    private val formulaMapper: Mapper<Formula, FileFormula> = DefaultFormulaMapper()
) : AggregationMapper {
    override fun <K : Comparable<K>> toFile(
        toFileMapping: ToFileMapping,
        src: Calculation.Aggregation<K>
    ): FileCalculation {
        return FileCalculation(
            name = src.name(),
            id = toFileMapping.idFor(src.id),
            formula = formulaMapper.toFile(toFileMapping, src.formula()),
            destination = toFileMapping.idFor(src.destination().id)
        )
    }

    override fun <K : Comparable<K>> toModel(
        toModelMapping: ToModelMapping,
        indexType: KClass<K>,
        src: FileCalculation
    ): Calculation.Aggregation<K> {
        return Calculation.Aggregation(
            indexType = indexType,
            name = src.name,
            formula = formulaMapper.toModel(toModelMapping, src.formula),
            destination = SingleValueId(toModelMapping.nextId(src.destination, SingleValueId::class)),
            id = toModelMapping.nextId(src.id, Calculation::class)
        )
    }
}