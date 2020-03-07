package de.flapdoodle.tab.bindings

import de.flapdoodle.tab.bindings.Bindings.OnlyNonNullBinding
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.Property
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import org.fxmisc.easybind.monadic.MonadicBinding

fun <T: Any> ObservableValue<T>.mapOnlyNonNull(): MonadicBinding<T> {
  return OnlyNonNullBinding(this)
}

fun <S : Any, T : Any> ObservableValue<S>.mapNullable(map: (S?) -> T?): MonadicBinding<T> {
  return Bindings.map(this, map)
}

fun <S : Any, T : Any> ObservableValue<S>.mapNonNull(map: (S) -> T): MonadicBinding<T> {
  return Bindings.map(this) {
    require(it!=null) {"source is null in $this"}
    map(it)
  }
}

fun <A : Any, B : Any, T : Any> ObservableValue<A>.mergeWith(other: ObservableValue<B>, map: (A, B) -> T): MonadicBinding<T> {
  return Bindings.combine(this, other) { a, b ->
    require(a != null) { "a is null for $map" }
    require(b != null) { "b is null for $map" }
    map(a, b)
  }
}

fun <S : Any, T : Any> Property<T>.mapFrom(src: ObservableValue<S>, map: (S?) -> T?) {
  this.bind(src.mapNullable(map))
}

fun <S : Any, D : Any> ObservableValue<S>.mapToList(map: (S) -> List<D?>): ObservableList<D> {
  return ToListBinding.newInstance(this,map)
}

fun <S : Any, D : Any> ObservableList<S>.mapNullable(map: (S?) -> D?): ObservableList<D> {
  return MappingListBinding.newInstance(this,map)
}

fun <S : Any, D : Any> ObservableList<S>.map(map: (S) -> D): ObservableList<D> {
  return MappingListBinding.newInstance(this) {
    require(it != null) {"source is null"}
    map(it)
  }
}

fun <S: Any, D: Any> ObservableList<S>.flatMapObservable(map: (S?) -> List<D?>): ObservableList<D> {
  return FlatmapListBinding.newInstance(this) {
    require(it != null) {"source is null"}
    map(it)
  }
}

fun <S: Any, D: Any> ObservableList<S>.mapTo(map: (List<S?>) -> D?): ObservableValue<D> {
  return SingleFromListBinding(this, map)
}

object Bindings {

  fun <T, U> map(
      src: ObservableValue<T>,
      f: (T?) -> U?): MonadicBinding<U> {
    return object : PreboundBinding<U>(src) {
      override fun computeValue(): U? {
        return f(src.value)
      }
    }
  }

  fun <A, B, R> combine(
      src1: ObservableValue<out A>,
      src2: ObservableValue<out B>,
      f: (A?, B?) -> R): MonadicBinding<R> {
    return object : PreboundBinding<R>(src1, src2) {
      override fun computeValue(): R {
        return f(src1.value, src2.value)
      }
    }
  }

  abstract class ObservableListWrapper<T>(
      private val delegate: ObservableList<T>
  ) : ObservableList<T> by delegate

  class OnlyNonNullBinding<T>(
      private val src: ObservableValue<T>
  ) : ObjectBinding<T>(), MonadicBinding<T> {

    private var v: T = src.value

    private val changeToInvalidListener = ChangeListener<T> { _,_,new ->
      if (new!=null) {
        v = new
        invalidate()
      }
    }

    init {
      bind(src)
      val weakListener = changeToInvalidListener.wrapByWeakChangeListener()
      src.addListener(weakListener)
    }

    override fun computeValue(): T {
      return v
    }

    override fun dispose() {
      unbind(src)
    }
  }

}