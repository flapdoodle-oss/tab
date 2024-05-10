package de.flapdoodle.tab.model.calculations.interpolation

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.calculations.interpolation.linear.LinearInterpolation
import kotlin.reflect.KClass

object DefaultInterpolatorFactoryLookup : InterpolatorFactoryLookup {
    override fun <K : Comparable<K>, V: Any> interpolatorFactoryFor(
        type: InterpolationType,
        indexType: TypeInfo<in K>,
        valueType: TypeInfo<V>
    ): InterpolatorFactory<in K, V> {
        val interpolatorFactory = when (type) {
            InterpolationType.Linear -> {
                val interpolation = LinearInterpolation.interpolation(indexType, valueType)
                interpolation?.let { LinearInterpolatorFactory(valueType, it) }
            }
            InterpolationType.LastValue -> LastValueInterpolatorFactory(valueType)
            InterpolationType.None -> NoInterpolation(valueType)
        }

        return interpolatorFactory ?: NoInterpolation(valueType)
    }
}