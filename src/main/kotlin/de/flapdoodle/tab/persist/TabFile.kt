package de.flapdoodle.tab.persist

data class TabFile(
    val model: PersistableTabModel,
    val nodePositions: PersistableNodePositions
)