package de.flapdoodle.tab.core

// list of Columns
// Column -> list of Index -> Value
data class IndexedValues<I : Any>(
    val indexType: IndexType<I>,
    val values: Map<Pair<I, Column<out Any>>, Any> = emptyMap()
) {

    private val indexList = values.keys
        .map(Pair<I, Column<out Any>>::first)
        .toSortedSet(indexType.comparator())

    private val columns = values.keys
        .map(Pair<I, Column<out Any>>::second)
        .toSet()

    fun <C : Any> put(
        column: Column<C>,
        changes: List<Pair<I, C?>>
    ): IndexedValues<I> {
        var copy = values
        changes.forEach {
            val value = it.second
            copy = if (value != null) {
                copy + ((it.first to column) to value)
            } else {
                copy - (it.first to column)
            }
        }
        return copy(values = copy)
    }

    fun <C : Any> put(
        column: Column<C>,
        vararg values: Pair<I, C?>
    ): IndexedValues<I> {
        return put(column, listOf(*values))
    }

    fun indexList() = indexList
    fun columns() = columns
}