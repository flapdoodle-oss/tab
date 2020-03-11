package de.flapdoodle.tab.persist

interface PersistableAdapter<S: Any, T: Any> : ToPersistable<S,T>, FromPersistable<S,T> {
}