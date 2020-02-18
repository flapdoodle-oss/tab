package de.flapdoodle.tab.graph.nodes

import de.flapdoodle.tab.graph.events.IsMarker

data class Resize(val parent: Resizeable) : IsMarker