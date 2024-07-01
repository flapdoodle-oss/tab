package de.flapdoodle.tab.model.calculations.adapter.index

import de.flapdoodle.eval.core.evaluables.Evaluated
import de.flapdoodle.reflection.TypeInfo

class InterpolatedEvaluated<K: Any, T>(
    private val index: K,
    private val delegate: Evaluated<T>,
) : Evaluated<T>() {
    fun index() = index

    override fun type(): TypeInfo<T> {
        return delegate.type()
    }

    override fun wrapped(): T {
        return delegate.wrapped()
    }
}