package de.flapdoodle.tab.bindings

import javafx.beans.Observable
import javafx.beans.binding.ObjectBinding
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.beans.value.WeakChangeListener
import org.fxmisc.easybind.monadic.MonadicBinding

/**
 * Object binding that binds to its dependencies on creation
 * and unbinds from them on dispose.
 */
abstract class PreboundBinding<T>(
    private vararg val dependencies: ObservableValue<*>
) : ObjectBinding<T>(), MonadicBinding<T> {

  private val changeToInvalidListener = ChangeListener<Any> { _,_,_ ->
    invalidate()
  }

  init {
    dependencies.forEach { it.addListener(changeToInvalidListener.wrap(::WeakChangeListener)) }
  }

  override fun dispose() {
    unbind(*dependencies)
  }
}
