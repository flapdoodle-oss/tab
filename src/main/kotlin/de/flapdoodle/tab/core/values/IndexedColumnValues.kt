package de.flapdoodle.tab.core.values

data class IndexedColumnValues<I : Any, C: Any>(
    val indexType: IndexType<I>,
    val columnType: ColumnType<C>,
    val values: Map<I, C> = emptyMap()
) {
    private val indexList = values.keys
        .toSortedSet(indexType.comparator())

    fun change(
        changes: List<Pair<I, C?>>
    ): IndexedColumnValues<I, C> {
        var copy = values
        changes.forEach {
            val value = it.second
            copy = if (value != null) {
                copy + (it.first to value)
            } else {
                copy - (it.first)
            }
        }
        return copy(values = copy)
    }

    fun change(
        vararg values: Pair<I, C?>
    ): IndexedColumnValues<I, C> {
        return change(listOf(*values))
    }

    fun get(
        indexList: List<I>
    ): List<C?> {
        return indexList.map { values[it] }
    }

    fun get(
        vararg index: I
    ): List<C?> {
        return get(listOf(*index))
    }

    fun indexList() = indexList
}