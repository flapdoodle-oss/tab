package de.flapdoodle.tab.extensions

import javafx.scene.control.TableColumn
import kotlin.reflect.KClass

fun <T : Any> TableColumn<out Any, out Any>.property(key: KClass<T>, value: T?) {
  ObservableMapExtensions.set(this.properties, key, value)
}

fun <T : Any> TableColumn<out Any, out Any>.property(key: KClass<T>): T? {
  return ObservableMapExtensions.get(this.properties, key)
}
