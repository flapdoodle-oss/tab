package de.flapdoodle.tab.observable

import de.flapdoodle.tab.bindings.Bindings
import javafx.beans.binding.ObjectBinding
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.fxmisc.easybind.monadic.MonadicBinding

fun <S : Any, D : Any> AObservable<S>.mapNonNull(map: (S) -> D?): AObservable<D> {
  return KObservables.mapNonNull(this, map)
}

fun <S : Any, D : Any> AObservable<S>.map(map: (S) -> D): AObservable<D> {
  return KObservables.map(this, map)
}

fun <S : Any, D : Any> AObservable<List<S>>.mapList(map: (S) -> D): AObservable<List<D>> {
  return KObservables.map(this) {
    it.map(map)
  }
}

fun <S : Any, D : Any> AObservable<S>.mapToList(map: (S) -> List<D>): AObservable<List<D>> {
  return KObservables.map(this) {
    map(it)
  }
}

fun <A : Any, B : Any, D : Any> AObservable<A>.merge(other: AObservable<B>, map: (A, B) -> D): AObservable<D> {
  return KObservables.merge(this, other, map)
}

fun <T : Any> AObservable<T>.asBinding(): MonadicBinding<T> {
  return KObservables.AObservableAsBinding(this)
}

fun <T : Any> AObservable<List<T>>.asListBinding(): ObservableList<T> {
  return KObservables.asListBinding(this)
}


object KObservables {
  fun <S : Any, D : Any> map(src: AObservable<S>, map: (S) -> D): AObservable<D> {
    val ret = ChangeableObservable(map(src.value()))
    val changeListener = ChangeListener<S> { _, old, new ->
      if (old != new) {
        ret.value(map(new))
      }
    }
    src.addListener(WeakChangeListenerDelegate(changeListener))
    return AObservable.Wrapper(ret, listOf(src, changeListener))
  }

  fun <S : Any, D : Any> mapNonNull(src: AObservable<S>, map: (S) -> D?): AObservable<D> {
    val initialValue = map(src.value())
    require(initialValue!=null) {"initial value is null"}

    val ret = ChangeableObservable(initialValue)
    val changeListener = ChangeListener<S> { _, old, new ->
      if (old != new) {
        val mapped = map(new)
        if (mapped!=null) {
          ret.value(mapped)
        }
      }
    }
    src.addListener(WeakChangeListenerDelegate(changeListener))
    return AObservable.Wrapper(ret, listOf(src, changeListener))
  }

  fun <A : Any, B : Any, D : Any> merge(a: AObservable<A>, b: AObservable<B>, map: (A, B) -> D): AObservable<D> {
    val ret = ChangeableObservable(map(a.value(), b.value()))
    val changeListenerA = ChangeListener<A> { _, _, newA ->
      ret.value(map(newA, b.value()))
    }
    val changeListenerB = ChangeListener<B> { _, _, newB ->
      ret.value(map(a.value(), newB))
    }
    a.addListener(WeakChangeListenerDelegate(changeListenerA))
    b.addListener(WeakChangeListenerDelegate(changeListenerB))
    return AObservable.Wrapper(ret, listOf(a, b, changeListenerA, changeListenerB))
  }

  fun <T: Any> asListBinding(src: AObservable<List<T>>) : ObservableList<T> {
    val ret = FXCollections.observableArrayList<T>()
    val listener = ChangeListener<List<T>> { _, _, list ->
      ret.setAll(list)
    }
    ret.setAll(src.value())
    src.addListener(WeakChangeListenerDelegate(listener))
    return Wrapper(
        delegate = ret,
        listener = listener,
        source = src
    )
  }

  class AObservableAsBinding<T : Any>(
      private val src: AObservable<T>
  ) : ObjectBinding<T>(), MonadicBinding<T> {

    val listener = ChangeListener<T> { _, _, _ ->
      invalidate()
    }

    init {
      src.addListener(WeakChangeListenerDelegate(listener))
    }

    override fun computeValue(): T {
      return src.value()
    }
  }

  class Wrapper<T>(
      delegate: ObservableList<T>,
      private val listener: ChangeListener<out Any>,
      private val source: AObservable<out Any>
  ) : Bindings.ObservableListWrapper<T>(delegate)
}