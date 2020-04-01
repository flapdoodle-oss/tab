package de.flapdoodle.tab.extensions

import javafx.collections.ObservableMap
import kotlin.reflect.KClass

object ObservableMapExtensions {
  fun <T: Any> get(map: ObservableMap<Any, Any>, key: Key<T>): T? {
    @Suppress("UNCHECKED_CAST")
    return map[key] as T?
  }

  fun <T: Any> set(map: ObservableMap<Any, Any>, key: Key<T>, value: T?): T? {
    @Suppress("UNCHECKED_CAST")
    return map.put(key,value) as T?
  }
}