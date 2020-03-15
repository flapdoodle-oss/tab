package de.flapdoodle.tab.extensions

import javafx.scene.Node
import javafx.scene.Parent
import kotlin.reflect.KClass

fun <T : Any> Node.property(scope: KClass<out Any>, key: KClass<T>, value: T?): T? {
  return ObservableMapExtensions.set(this.properties, scope,key, value)
}

fun <T : Any> Node.property(scope: KClass<out Any>, key: KClass<T>): T? {
  return ObservableMapExtensions.get(this.properties, scope, key)
}

fun <T : Any> Node.property(key: KClass<T>, value: T?): T? {
  return ObservableMapExtensions.set(this.properties, key, value)
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

fun Node.parentPath(child: Node): List<Node> {
  return if (child.parent == this) {
    listOf(this, child)
  } else {
    val parentOfChild = child.parent
    return this.parentPath(parentOfChild) + child
  }
}