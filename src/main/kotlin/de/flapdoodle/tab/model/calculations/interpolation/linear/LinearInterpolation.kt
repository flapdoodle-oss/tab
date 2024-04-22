package de.flapdoodle.tab.model.calculations.interpolation.linear

import java.math.BigDecimal
import kotlin.reflect.KClass

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
            val indexType: KClass<K>,
            val valueType: KClass<V>,
            val interpolation: LinearInterpolation<K, V>
        )

        private val interpolations = listOf(
            InterpolationEntry(Int::class, Int::class, asInterpolation(LinearFactor.IntFactor, FactorMultiplicator.IntDoubleMultiplicator)),
            InterpolationEntry(Int::class, BigDecimal::class, asInterpolation(LinearFactor.IntFactor, FactorMultiplicator.BigDecimalDoubleMultiplicator)),
        )

        private val interpolationMap: Map<Pair<KClass<out Any>, KClass<out Any>>, LinearInterpolation<Int, out Any>> = interpolations.map {
            (it.indexType to it.valueType) to it.interpolation
        }.toMap()

        fun <K : Any, V : Any> interpolation(indexType: KClass<K>, valueType: KClass<V>): LinearInterpolation<K, V>? {
            return interpolationMap[indexType to valueType] as LinearInterpolation<K, V>?
        }
    }
}