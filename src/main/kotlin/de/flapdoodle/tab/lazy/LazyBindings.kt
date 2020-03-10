package de.flapdoodle.tab.lazy

import de.flapdoodle.tab.bindings.Registration
import de.flapdoodle.tab.observable.AObservable
import de.flapdoodle.tab.observable.KObservableBindings
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.collections.WeakListChangeListener
import javafx.scene.Node

fun <S : Any, D : Node> ObservableList<Node>.bindFrom(src: LazyValue<List<S>>, map: (S) -> D): Registration {
  return LazyBindings.bindFrom(src, this, map)
}

fun <S : Any, D : Any> ObservableList<D>.syncFrom(src: LazyValue<List<S>>, map: (S) -> D): Registration {
  return LazyBindings.syncFrom(src, this, map)
}

fun <S: Any> ObservableList<S>.asAObservable(): LazyValue<List<S>> {
  return LazyBindings.asAObservable(this)
}

object LazyBindings {
  fun <S : Any, D : Node> bindFrom(src: LazyValue<List<S>>, children: ObservableList<Node>, map: (S) -> D): Registration {
    val listener = NodeFactoryChangeListener(src, children, map)
    val weakListener = WeakChangeListenerDelegate(listener)
    val keepReferenceListener = KeepReference<Node>(listener)

    src.addListener(weakListener)
    children.addListener(keepReferenceListener)

    return Registration {
      src.removeListener(weakListener)
      children.removeListener(keepReferenceListener)
    }
  }

  fun <S : Any, D : Any> syncFrom(src: LazyValue<List<S>>, children: ObservableList<D>, map: (S) -> D): Registration {
    children.setAll(src.value().map(map))

    val listener = ChangedListener<List<S>> { _ ->
      val mapped = src.value().map(map)
      children.setAll(mapped)
    }
    val weakListener = WeakChangeListenerDelegate(listener)
    val keepReferenceListener = KeepReference<D>(listOf(src,listener))

    src.addListener(weakListener)
    children.addListener(keepReferenceListener)

    return Registration {
      src.removeListener(weakListener)
      children.removeListener(keepReferenceListener)
    }
  }

  fun <S: Any> asAObservable(src: ObservableList<S>): LazyValue<List<S>> {
    val ret = ChangeableValue<List<S>>(src.toList())
    val listener = ListChangeListener<S> {
      ret.value(it.list)
    }
    val weakListener = WeakListChangeListener(listener)
    src.addListener(weakListener)

    return LazyValue.Wrapper(ret, listener)
  }

  class NodeFactoryChangeListener<S : Any, D : Node>(
      private val src: LazyValue<List<S>>,
      private val children: ObservableList<Node>,
      private val map: (S) -> D
  ) : ChangedListener<List<S>> {

    private var mapped = src.value().map { it to map(it) }

    init {
      children.addAll(mapped.map { it.second })
    }

    override fun hasChanged(value: LazyValue<List<S>>) {
      val new = src.value()

      val current = mapped
      val currentSources = current.map { it.first }

      val removed = current.filter { !new.contains(it.first) }
      children.removeAll(removed.map { it.second })

      val newIds = new.filter { !currentSources.contains(it) }
      val added = newIds.map { it to map(it) }
      children.addAll(added.map { it.second })

      mapped = current - removed + added
    }
  }

  class KeepReference<T>(
      private val reference: Any
  ) : ListChangeListener<T> {
    override fun onChanged(c: ListChangeListener.Change<out T>?) {}
  }
}