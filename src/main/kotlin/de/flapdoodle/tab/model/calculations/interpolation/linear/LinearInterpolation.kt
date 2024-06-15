package de.flapdoodle.tab.model.calculations.interpolation.linear

import de.flapdoodle.reflection.TypeInfo
import java.math.BigDecimal

fun interface LinearInterpolation<K : Any, V : Any> {
    fun interpolate(start: Pair<K, V>, end: Pair<K, V>, offset: K): V

    companion object {
        private fun <K : Any, V : Any, F : Any> asInterpolation(
            linearFactor: LinearFactor<K, F>,
            multiplicator: FactorMultiplicator<V, F>
        ): LinearInterpolation<K, V> {
            return LinearInterpolation { start, end, offset ->
                val factor = linearFactor.factor(start.first, end.first, offset)
                multiplicator.multiply(start.second, end.second, factor)
            }
        }

        private data class InterpolationEntry<K : Any, V : Any>(
            val indexType: TypeInfo<K>,
            val valueType: TypeInfo<V>,
            val interpolation: LinearInterpolation<K, V>
        )

        private val interpolations = listOf(
            InterpolationEntry(TypeInfo.of(Int::class.javaObjectType), TypeInfo.of(Int::class.javaObjectType), asInterpolation(LinearFactor.IntFactor, FactorMultiplicator.IntDoubleMultiplicator)),
            InterpolationEntry(TypeInfo.of(Int::class.javaObjectType), TypeInfo.of(BigDecimal::class.javaObjectType), asInterpolation(LinearFactor.IntFactor, FactorMultiplicator.BigDecimalDoubleMultiplicator)),
        )

        private val interpolationMap: Map<Pair<TypeInfo<out Any>, TypeInfo<out Any>>, LinearInterpolation<Int, out Any>> = interpolations.map {
            (it.indexType to it.valueType) to it.interpolation
        }.toMap()

        fun <K : Any, V : Any> interpolation(indexType: TypeInfo<K>, valueType: TypeInfo<V>): LinearInterpolation<K, V>? {
            return interpolationMap[indexType to valueType] as LinearInterpolation<K, V>?
        }
    }
}