package de.flapdoodle.tab.persist

fun interface FromPersistable<T : Any, S : Any> {
    fun from(context: FromPersistableContext, source: S): T
}