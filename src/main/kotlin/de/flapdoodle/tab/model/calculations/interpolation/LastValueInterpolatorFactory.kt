package de.flapdoodle.tab.model.calculations.interpolation

import de.flapdoodle.eval.core.evaluables.Evaluated
import de.flapdoodle.reflection.TypeInfo
import kotlin.reflect.KClass

class LastValueInterpolatorFactory<K: Comparable<K>, V: Any>(
    val valueType: TypeInfo<V>
) : InterpolatorFactory<K, V> {

    override fun interpolatorFor(index: Set<K>, values: Map<out K, V>): Interpolator<K, V> {
        val index = values.keys.sorted()
        
        return Interpolator { offset ->
            val firstIndex = index.lastOrNull {
                it <= offset
            }
            if (firstIndex!=null)
                Evaluated.ofNullable(valueType, values[firstIndex])
            else
                Evaluated.ofNull(valueType)
        }

    }
}