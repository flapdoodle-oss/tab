package de.flapdoodle.tab.model

sealed class Value<V> {

    class Multi<IDX, V>(
        val index: List<IDX>,
        val values: List<V?>
    ) : Value<V>() where IDX : Comparable<IDX> {
        init {
            require(index.size == values.size) { "size missmatch: ${index.size} != ${values.size}" }
        }
    }

    class Single<V>(value: V?) : Value<V>()
}