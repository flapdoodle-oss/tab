package de.flapdoodle.tab.io.mapper

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.io.adapter.ToFileMapping
import de.flapdoodle.tab.io.adapter.ToModelMapping
import de.flapdoodle.tab.io.file.FileCalculation
import de.flapdoodle.tab.io.file.FileFormula
import de.flapdoodle.tab.model.calculations.Calculation
import de.flapdoodle.tab.model.calculations.Formula
import de.flapdoodle.tab.model.data.ColumnId
import kotlin.reflect.KClass

class DefaultTabularMapper(
    private val formulaMapper: Mapper<Formula, FileFormula> = DefaultFormulaMapper()
) : TabularMapper {
    override fun <K : Comparable<K>> toFile(
        toFileMapping: ToFileMapping,
        src: Calculation.Tabular<K>
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
        indexType: TypeInfo<K>,
        src: FileCalculation
    ): Calculation.Tabular<K> {
        return Calculation.Tabular(
            indexType = indexType,
            name = src.name,
            formula = formulaMapper.toModel(toModelMapping, src.formula),
            destination = ColumnId(toModelMapping.nextId(src.destination, ColumnId::class)),
            id = toModelMapping.nextId(src.id, Calculation::class)
        )
    }
}