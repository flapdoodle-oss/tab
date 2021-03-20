package de.flapdoodle.tab.model

data class ColumnValues<K: Any, T>(val values: Map<K,T>) {
}