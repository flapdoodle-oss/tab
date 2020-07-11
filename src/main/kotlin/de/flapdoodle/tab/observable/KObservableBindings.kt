package de.flapdoodle.tab.observable

import de.flapdoodle.fx.bindings.Registration
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.collections.WeakListChangeListener
import javafx.scene.Node

fun <S : Any, D : Node> ObservableList<Node>.bindFrom(src: AObservable<List<S>>, map: (S) -> D): Registration {
  return KObservableBindings.bindFrom(src, this, map)
}

fun <S : Any, D : Any> ObservableList<D>.syncFrom(src: AObservable<List<S>>, map: (S) -> D): Registration {
  return KObservableBindings.syncFrom(src, this, map)
}

fun <S: Any> ObservableList<S>.asAObservable(): AObservable<List<S>> {
  return KObservableBindings.asAObservable(this)
}

object KObservableBindings {

  fun <S : Any, D : Node> bindFrom(src: AObservable<List<S>>, children: ObservableList<Node>, map: (S) -> D): Registration {
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

  fun <S : Any, D : Any> syncFrom(src: AObservable<List<S>>, children: ObservableList<D>, map: (S) -> D): Registration {
    children.setAll(src.value().map(map))

    val listener = ChangeListener<List<S>> { _, _, new ->
      val mapped = new.map(map)
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

  fun <S: Any> asAObservable(src: ObservableList<S>): AObservable<List<S>> {
    val ret = ChangeableObservable<List<S>>(src.toList())
    val listener = ListChangeListener<S> {
      ret.value(it.list)
    }
    val weakListener = WeakListChangeListener(listener)
    src.addListener(weakListener)

    return AObservable.Wrapper(ret, listener)
  }

  class NodeFactoryChangeListener<S : Any, D : Node>(
      private val src: AObservable<List<S>>,
      private val children: ObservableList<Node>,
      private val map: (S) -> D
  ) : ChangeListener<List<S>> {

    private var mapped = src.value().map { it to map(it) }

    init {
      children.addAll(mapped.map { it.second })
    }

    override fun changed(src: AObservable<List<S>>, old: List<S>, new: List<S>) {
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