package de.flapdoodle.tab.bindings

import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import java.util.Collections

class MappingListChangeListener<S, D>(
    private val dst: ObservableList<D>,
    private val map: (S?) -> D?
) : ListChangeListener<S> {
  companion object {
    private fun debug(msg: () -> String) {
      if (false) {
        println(msg())
      }
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

    /**
     * maybe more optimized
     */
    private fun <T> permutate(from: Int, to: Int, newIndex: (Int) -> Int, list: ObservableList<T>) {
      val iterator = list.listIterator(from)
      val array = ArrayList(list)
      for (idx in from until to) {
//        val element = array[]
      }
//      fun sort(c: Comparator<in E?>?) {
//        val a: Array<Any> = this.toTypedArray()
//        Arrays.sort(a, c)
//        val iterator: MutableListIterator<E> = this.listIterator()
//        val size = a.size
//        for (idx in 0 until size) {
//          val e = a[idx]
//          iterator.next()
//          iterator.set(e)
//        }
//      }

    }
  }

  override fun onChanged(change: ListChangeListener.Change<out S>) {
    val src = change.list

    while (change.next()) {
      debug { "change: $change" }
      if (change.wasPermutated()) {
        if (true) {
          permutationToSwap(change.from, change.to, change::getPermutation) { a, b ->
            Collections.swap(dst, a, b)
          }
        } else {
          permutate(change.from, change.to, change::getPermutation, dst)
        }
      } else if (change.wasUpdated()) {
        (change.from until change.to).forEach {
          dst[it] = map(src[it])
        }
      } else if (change.wasReplaced()) {
        debug { "-> ${change.from} : ${change.to} (src size: ${src.size})" }
        if (change.from == change.to - 1) {
          // single element
          dst.set(change.from, map(src[change.from]))
        } else {
          if (change.from == 0 && change.to==src.size) {
            dst.setAll(src.map(map))
          } else {
            if (true) throw IllegalArgumentException("not supported: $change")
//            dst.remove(change.from, change.to)
//            dst.addAll(src.subList(change.from, change.to).map(map))
          }
        }

      } else {
        if (change.wasRemoved()) {
          require(!change.wasAdded()) { "change was added is not expected here: $change" }
          require(change.from == change.to) {"should be just one element: $change"}
          debug { "-> ${change.from} : ${change.to}" }
          dst.remove(change.from, change.to + 1)
        }
        if (change.wasAdded()) {
          dst.addAll(src.subList(change.from, change.to).map(map))
        }
      }
    }
  }
}