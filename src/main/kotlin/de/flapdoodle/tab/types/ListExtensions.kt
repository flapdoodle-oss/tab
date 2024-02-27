package de.flapdoodle.tab.types

fun <T> List<T>.one(predicate: (T) -> Boolean): T {
    val filtered = filter(predicate)
    require(filtered.size==1) { "more or less then one match for $predicate($this): $filtered "}
    return filtered[0]
}

fun <T> List<T>.change(predicate: (T) -> Boolean, change: (T) -> T): List<T> {
    return map { if (predicate(it)) change(it) else it }
}

fun <T, K> List<T>.change(keyOf: (T) -> K, match: K, change: (T) -> T): List<T> {
    return map { if (keyOf(it) == match) change(it) else it }
}