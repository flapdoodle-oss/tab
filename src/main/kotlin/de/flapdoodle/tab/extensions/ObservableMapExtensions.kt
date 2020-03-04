package de.flapdoodle.tab.extensions

import com.sun.source.tree.Scope
import javafx.collections.ObservableMap
import javafx.scene.Node
import kotlin.reflect.KClass

object ObservableMapExtensions {
  fun <T: Any> get(map: ObservableMap<Any, Any>, key: KClass<T>): T? {
    @Suppress("UNCHECKED_CAST")
    return map[Wrapper(key)] as T?
  }

  fun <T: Any> set(map: ObservableMap<Any, Any>, key: KClass<T>, value: T?): T? {
    @Suppress("UNCHECKED_CAST")
    return map.put(Wrapper(key),value) as T?
  }

  fun <T: Any> get(map: ObservableMap<Any, Any>, scope: KClass<out Any>, key: KClass<T>): T? {
    @Suppress("UNCHECKED_CAST")
    return map[ScopedWrapper(scope, key)] as T?
  }

  fun <T: Any> set(map: ObservableMap<Any, Any>, scope: KClass<out Any>, key: KClass<T>, value: T?): T? {
    @Suppress("UNCHECKED_CAST")
    return map.put(ScopedWrapper(scope, key),value) as T?
  }

  private data class Wrapper<T: Any>(private val type: KClass<T>)
  private data class ScopedWrapper<T: Any>(private val scope: KClass<out Any>, private val type: KClass<T>)
}