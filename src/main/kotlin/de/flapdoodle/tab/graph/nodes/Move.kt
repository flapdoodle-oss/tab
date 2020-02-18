package de.flapdoodle.tab.graph.nodes

import de.flapdoodle.tab.graph.events.IsMarker

data class Move(val parent: Moveable) : IsMarker