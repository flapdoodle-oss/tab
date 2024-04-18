package de.flapdoodle.tab.app.io.mapper

import de.flapdoodle.tab.app.io.adapter.ToFileMapping
import de.flapdoodle.tab.app.io.adapter.ToModelMapping
import de.flapdoodle.tab.app.io.file.FileCalculation
import de.flapdoodle.tab.app.model.calculations.Calculation
import de.flapdoodle.tab.app.model.calculations.adapter.EvalFormulaAdapter
import de.flapdoodle.tab.app.model.data.ColumnId
import de.flapdoodle.tab.app.model.data.SingleValueId
import kotlin.reflect.KClass

class DefaultCalculationMapper : CalculationMapper {
    override fun <K: Comparable<K>> toFile(toFileMapping: ToFileMapping, src: Calculation<K>): FileCalculation {
        val base = FileCalculation(
            name = src.name(),
            id = toFileMapping.idFor(src.id),
            expression = src.formula().expression()
        )

        return when (src) {
            is Calculation.Aggregation<K> -> {
                base.copy(aggregation = FileCalculation.Aggregation(
                    destination = toFileMapping.idFor(src.destination().id)
                ))
            }
            is Calculation.Tabular<K> -> {
                base.copy(tabular = FileCalculation.Tabular(
                    destination = toFileMapping.idFor(src.destination().id)
                ))
            }
        }
    }

    override fun <K : Comparable<K>> toModel(
        toModelMapping: ToModelMapping,
        indexType: KClass<K>,
        src: FileCalculation
    ): Calculation<K> {
        require(src.aggregation!=null || src.tabular!=null) { "no aggregation or tabular"}
        return if (src.aggregation != null) {
            Calculation.Aggregation(
                indexType = indexType,
                name = src.name,
                // TODO move to toModelMapping
                formula = EvalFormulaAdapter(src.expression),
                destination = SingleValueId(toModelMapping.nextId(src.aggregation.destination, SingleValueId::class)),
                id = toModelMapping.nextId(src.id, Calculation::class)
            )
        } else {
            Calculation.Tabular(
                indexType = indexType,
                name = src.name,
                // TODO move to toModelMapping
                formula = EvalFormulaAdapter(src.expression),
                destination = ColumnId(toModelMapping.nextId(src.tabular!!.destination, ColumnId::class)),
                id = toModelMapping.nextId(src.id, Calculation::class)
            )
        }
    }
}