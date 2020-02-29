package de.flapdoodle.tab.bindings

import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import org.fxmisc.easybind.EasyBind
import org.fxmisc.easybind.monadic.MonadicBinding

fun <S : Any, T : Any> ObservableValue<S>.mapNullable(map: (S?) -> T?): MonadicBinding<T> {
  return EasyBind.map(this, map)
}

fun <A : Any, B : Any, T : Any> ObservableValue<A>.mergeWith(other: ObservableValue<B>, map: (A, B) -> T): MonadicBinding<T> {
  return EasyBind.combine(this, other) { a, b ->
    require(a != null) { "a is null" }
    require(b != null) { "b is null" }
    map(a, b)
  }
}

fun <S : Any, T : Any> Property<T>.mapFrom(src: ObservableValue<S>, map: (S?) -> T?) {
  this.bind(src.mapNullable(map))
}

fun <S : Any, D : Any> ObservableValue<S>.mapToList(map: (S) -> List<D?>): ObservableList<D> {
  return ToListBinding(this,map)
}

fun <S : Any, D : Any> ObservableList<S>.mapNullable(map: (S?) -> D?): ObservableList<D> {
  return MappingListBinding(this,map)
}

fun <S : Any, D : Any> ObservableList<S>.map(map: (S) -> D): ObservableList<D> {
  return MappingListBinding(this) {
    require(it != null) {"source in $this is null"}
    map(it)
  }
}

fun <S: Any, D: Any> ObservableList<S>.flatMapObservable(map: (S?) -> List<D?>): ObservableList<D> {
  return FlatmapListBinding(this) {
    require(it != null) {"source in $this is null"}
    map(it)
  }
}