package de.flapdoodle.tab.model.calculations.interpolation

import de.flapdoodle.eval.core.evaluables.Evaluated
import kotlin.reflect.KClass

fun interface Interpolator<K: Any, V: Any> {
    fun interpolated(offset: K): Evaluated<V>

    fun interpolatedAt(index: Set<K>): Map<K, Evaluated<V>> {
        return interpolate(index, this)
    }

    companion object {
        fun <K: Comparable<K>, V: Any> valueAtOffset(valueType: KClass<V>, values: Map<K, V>): Interpolator<K, V> {
            val index = values.keys.toSortedSet()
            return Interpolator { offset ->
                val firstIndex = index.lastOrNull {
                    it <= offset
                }
                if (firstIndex!=null)
                    Evaluated.ofNullable(valueType.javaObjectType, values[firstIndex])
                else
                    Evaluated.ofNull(valueType.javaObjectType)
            }
        }

        fun <K: Any, V: Any> interpolate(index: Set<K>, interpolator: Interpolator<K, V>): Map<K, Evaluated<V>> {
            return index.associateWith { interpolator.interpolated(it) }
        }
    }
}