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

  class TypedMap(
      private val map: ObservableMap<Any, Any>
  ) {
    operator fun <T: Any> set(key: Key<T>, value: T?): T? {
      return set(map, key, value)
    }

    operator fun <T: Any> get(key: Key<T>): T? {
      return get(map,key)
    }

    operator fun <T: Any> set(type: KClass<T>, value: T?): T? {
      return set(map, Key.ofType(type), value)
    }

    operator fun <T: Any> get(type: KClass<T>): T? {
      return get(map,Key.ofType(type))
    }
  }
}