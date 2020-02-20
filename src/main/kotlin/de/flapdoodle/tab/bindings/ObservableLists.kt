package de.flapdoodle.tab.bindings

import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import tornadofx.*

fun <S: Any, D: Any> ObservableValue<S>.mapToList(map: (S) -> List<D?>): ObservableLists.MappedObservableList<D> {
  return ObservableLists.mapToList(this, map)
}

fun <S: Any, D: Any> ObservableList<D>.syncFrom(src: ObservableList<S>, map: (S?) -> D?): Registration {
  return ObservableLists.addMappedSync(src, this, map)
}

object ObservableLists {

  private fun debug(msg: () -> String) {
    if (false) println(msg())
  }

  private fun permutationToSwap(from: Int, to: Int, newIndex: (Int) -> Int, swapAction: (Int, Int) -> Unit) {
    debug { "---------------------" }
    (from until to).forEach {
      debug { "move $it -> ${newIndex(it)}" }
    }
    val firstToSwap = (from until to).find { newIndex(it) != it }
    debug { "firstToSwap: $firstToSwap" }
    val swapsNeeded = (from until to).count { newIndex(it) != it } - 1
    debug { "loops: $swapsNeeded" }
    if (firstToSwap != null) {

      var start = firstToSwap!!
      val src = start

      (0 until swapsNeeded).forEach {
        val dst = newIndex(start)
        require(dst != start) { "$dst == $start" }
        debug { "$start ($src) -> $dst" }
        swapAction(src, dst)
        start = dst
      }
    }
    debug { "---------------------" }
  }

  fun <S : Any, D : Any> addMappedSync(src: ObservableList<S>, dst: ObservableList<D>, map: (S?) -> D?): Registration {
    val changeListener = ListChangeListener<S> { change ->
      while (change.next()) {
        debug { "change: $change" }
        if (change.wasPermutated()) {
          permutationToSwap(change.from, change.to, change::getPermutation) { a, b -> dst.swap(a, b) }
        } else if (change.wasUpdated()) {
          (change.from until change.to).forEach {
            dst[it] = map(src[it])
          }
        } else if (change.wasReplaced()) {
          (change.from until change.to).forEach {
            dst[it] = map(src[it])
          }
        } else {
          if (change.wasRemoved()) {
            require(!change.wasAdded()) { "change was added is not expected here: $change" }
            debug { "-> ${change.from} : ${change.to}" }
            dst.remove(change.from, change.to + 1)
          }
          if (change.wasAdded()) {
            (change.from until change.to).forEach {
              dst.add(it, map(src[it]))
            }
          }
        }

      }
    }

    dst.clear()
    src.forEach { dst.add(map(it)) }

    src.addListener(changeListener)

    return Registration {
      src.removeListener(changeListener)
    }
  }

  fun <S : Any, D : Any> map(src: ObservableList<S>, map: (S?) -> D?): MappedObservableList<D> {
    val ret = FXCollections.observableArrayList<D>()
    val registration = addMappedSync(src, ret, map)
    return MappedObservableList(ret, registration)
  }

  fun <S : Any, D : Any> mapToList(src: ObservableValue<S>, map: (S) -> List<D?>): MappedObservableList<D> {
    val ret = FXCollections.observableArrayList<D>()
    val changeListener = javafx.beans.value.ChangeListener<S> { _, _, newValue: S? ->
      val list = if (newValue != null) map(newValue) else emptyList()
      if (list.size < ret.size) {
        ret.remove(list.size, ret.size)
      }
      (ret.size until list.size).forEach {
        ret.add(list[it])
      }
      (0 until ret.size).forEach {
        ret[it] = list[it]
      }
    }

    ret.addAll(map(src.value))

    src.addListener(changeListener)
    val registration = Registration { src.removeListener(changeListener) }
    return MappedObservableList(ret, registration)
  }

  class MappedObservableList<T : Any>(
      wrapped: ObservableList<T>,
      private val registration: Registration
  ) : ObservableList<T> by wrapped
}
