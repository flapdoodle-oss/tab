package de.flapdoodle.tab.bindings

import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import tornadofx.*

class MappingListChangeListener<S,D>(
    private val dst: ObservableList<D>,
    private val map: (S?) -> D?
) : ListChangeListener<S> {
  companion object {
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

  }

  override fun onChanged(change: ListChangeListener.Change<out S>) {
    val src = change.list

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
}