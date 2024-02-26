package de.flapdoodle.tab.types

fun <T> List<T>.one(predicate: (T) -> Boolean): T {
    val filtered = filter(predicate)
    require(filtered.size==1) { "more or less then one match for $predicate($this): $filtered "}
    return filtered[0]
}