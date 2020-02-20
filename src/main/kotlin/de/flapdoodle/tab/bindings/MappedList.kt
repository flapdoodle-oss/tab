package de.flapdoodle.tab.bindings

import javafx.beans.value.ObservableObjectValue
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.collections.transformation.TransformationList
import java.util.ArrayList
import java.util.function.Function

fun <T: Any, S: Any> ObservableList<S>.mapped(transformation: (S?) -> T?): TransformationList<T, S> {
  return MappedList(this, transformation)
}

class MappedList<T: Any, S: Any>(source: ObservableList<out S>, private val mapper: (S?) -> T?) : TransformationList<T, S>(source) {
  override fun getSourceIndex(index: Int): Int {
    return index
  }

  override fun getViewIndex(index: Int): Int {
    return index
  }

  override fun get(index: Int): T? {
    return mapper(source[index])
  }

  override val size: Int
    get() = source.size

  override fun sourceChanged(c: ListChangeListener.Change<out S>) {
    fireChange(object : ListChangeListener.Change<T>(this) {
      override fun wasAdded(): Boolean {
        return c.wasAdded()
      }

      override fun wasRemoved(): Boolean {
        return c.wasRemoved()
      }

      override fun wasReplaced(): Boolean {
        return c.wasReplaced()
      }

      override fun wasUpdated(): Boolean {
        return c.wasUpdated()
      }

      override fun wasPermutated(): Boolean {
        return c.wasPermutated()
      }

      override fun getPermutation(i: Int): Int {
        return c.getPermutation(i)
      }

      override fun getPermutation(): IntArray { // This method is only called by the superclass methods
// wasPermutated() and getPermutation(int), which are
// both overriden by this class. There is no other way
// this method can be called.
        throw AssertionError("Unreachable code")
      }

      override fun getRemoved(): List<T?> {
        val res = ArrayList<T?>(c.removedSize)
        for (e in c.removed) {
          res.add(mapper(e))
        }
        return res
      }

      override fun getFrom(): Int {
        return c.from
      }

      override fun getTo(): Int {
        return c.to
      }

      override fun next(): Boolean {
        return c.next()
      }

      override fun reset() {
        c.reset()
      }
    })
  }

}