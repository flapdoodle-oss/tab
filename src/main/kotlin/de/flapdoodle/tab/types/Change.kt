package de.flapdoodle.tab.types

data class Change<T>(
    val removed: Set<T>,
    val notChanged: Set<T>,
    val modified: Set<Pair<T,T>>,
    val added: Set<T>
) {
    companion object {
        fun <K: Any, T: Any> diff(old: Collection<T>, new: Collection<T>, keyOf: (T) -> K): Change<T> {
            val oldByKey = old.associateBy(keyOf)
            val newByKey = new.associateBy(keyOf)

            val removed = oldByKey.keys - newByKey.keys
            val added = newByKey.keys - oldByKey.keys
            val stillThere = oldByKey.keys.intersect(newByKey.keys)

            val keyAnPair = stillThere.map { it to (oldByKey[it]!! to newByKey[it]!!) }
            val (notChanged, modified) = keyAnPair.partition { it.second.first == it.second.second }

            return Change(
                removed = removed.map { oldByKey[it]!! }.toSet(),
                notChanged = notChanged.map { it.second.first }.toSet(),
                modified = modified.map { it.second }.toSet(),
                added = added.map { newByKey[it]!! }.toSet()
            )
        }
    }
}