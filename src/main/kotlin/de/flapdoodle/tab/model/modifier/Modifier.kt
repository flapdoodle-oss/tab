package de.flapdoodle.tab.model.modifier

import de.flapdoodle.tab.model.Node

sealed class Modifier {
    abstract fun modify(nodes: List<Node>): List<Node>
}