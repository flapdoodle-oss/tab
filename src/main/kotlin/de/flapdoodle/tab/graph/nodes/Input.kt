package de.flapdoodle.tab.graph.nodes

import de.flapdoodle.tab.graph.events.IsMarker
import de.flapdoodle.tab.graph.events.Marker
import kotlin.reflect.KClass

data class Input<T: Any>(val type: KClass<T>) : IsMarker