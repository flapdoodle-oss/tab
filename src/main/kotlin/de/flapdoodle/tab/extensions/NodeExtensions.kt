package de.flapdoodle.tab.extensions

import de.flapdoodle.tab.graph.events.HasMarkerProperty
import de.flapdoodle.tab.graph.events.IsMarker
import javafx.scene.Node
import kotlin.reflect.KClass

fun <T : Any> Node.property(key: KClass<T>, value: T?) {
  ObservableMapExtensions.set(this.properties, key, value)
}

fun <T : Any> Node.property(key: KClass<T>): T? {
  return ObservableMapExtensions.get(this.properties, key)
}
