package de.flapdoodle.tab.app.model.diff

data class Diff<T>(
    val same: Set<T>,
    val changed: Set<Pair<T, T>>,
    val new: Set<T>,
    val removed: Set<T>
) {

    companion object {
        fun <T> diff(old: Collection<out T>, new: Collection<out T>): Diff<T> {
            return diff(old,new) { it }
        }

        fun <T, K> diff(old: Collection<out T>, new: Collection<out T>, keyOf: (T) -> K): Diff<T> {
            val oldByKey = old.associateBy(keyOf)
            val newByKey = new.associateBy(keyOf)

            val sameIds = oldByKey.keys.intersect(newByKey.keys)
            val oldIds = oldByKey.keys - newByKey.keys
            val newIds = newByKey.keys - oldByKey.keys

            val (unchanged, different) = sameIds.partition { oldByKey[it] == newByKey[it] }
            return Diff(
                same = unchanged.map { oldByKey[it]!! }.toSet(),
                changed = different.map { oldByKey[it]!! to newByKey[it]!! }.toSet(),
                new = newIds.map { newByKey[it]!! }.toSet(),
                removed = oldIds.map { oldByKey[it]!! }.toSet()
            )
        }
    }
}