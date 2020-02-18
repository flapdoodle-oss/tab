package de.flapdoodle.tab.graph.nodes

import de.flapdoodle.tab.graph.events.IsMarker
import kotlin.reflect.KClass

data class Output<T: Any>(val type: KClass<T>) : IsMarker