package de.flapdoodle.tab.extensions

import javafx.collections.ObservableMap
import javafx.scene.Node
import kotlin.reflect.KClass

object ObservableMapExtensions {
  fun <T: Any> get(map: ObservableMap<Any, Any>, key: KClass<T>): T? {
    @Suppress("UNCHECKED_CAST")
    return map[Wrapper(key)] as T?
  }

  fun <T: Any> set(map: ObservableMap<Any, Any>, key: KClass<T>, value: T?) {
    map[Wrapper(key)] = value
  }

  private data class Wrapper<T: Any>(private val type: KClass<T>)
}