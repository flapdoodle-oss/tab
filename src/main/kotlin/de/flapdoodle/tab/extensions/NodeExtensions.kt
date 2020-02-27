package de.flapdoodle.tab.extensions

import de.flapdoodle.tab.graph.events.HasMarkerProperty
import de.flapdoodle.tab.graph.events.IsMarker
import javafx.scene.Node
import javafx.scene.Parent
import kotlin.reflect.KClass

fun <T : Any> Node.property(key: KClass<T>, value: T?) {
  ObservableMapExtensions.set(this.properties, key, value)
}

fun <T : Any> Node.property(key: KClass<T>): T? {
  return ObservableMapExtensions.get(this.properties, key)
}

fun <T: Any> Parent.findAllInTree(type: KClass<T>): List<T> {
  return childrenUnmodifiable.flatMap {
    val nodeAsList = if (type.isInstance(it)) {
      @Suppress("UNCHECKED_CAST")
      listOf(it as T)
    } else
      emptyList()

    val sub = if (it is Parent) it.findAllInTree(type) else emptyList()

    nodeAsList + sub
  }
}
