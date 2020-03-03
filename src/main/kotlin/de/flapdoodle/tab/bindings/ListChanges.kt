package de.flapdoodle.tab.bindings

import javafx.collections.ObservableList

object ListChanges {
  fun <D> applyChanges(dst: ObservableList<D>, list: List<D?>) {
    if (list.size < dst.size) {
      dst.remove(list.size, dst.size)
    }
    (0 until dst.size).forEach {
      if (dst[it] != list[it]) {
        dst[it] = list[it]
      }
    }
    dst.addAll(list.subList(dst.size, list.size))
  }

}