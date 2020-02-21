package de.flapdoodle.tab.bindings

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.beans.value.WritableObjectValue
import java.util.concurrent.atomic.AtomicBoolean

fun <A : Any, B : Any, AT> AT.mapped(converter: Converter<A, B>): RegisteredWritableObservableValue<B>
    where AT : ObservableValue<A>,
          AT : WritableObjectValue<A> {
  return ObjectProperties.bidirectionalMappedSync(this, converter)
}

object ObjectProperties {

  fun <A : Any, B : Any, AT, BT> bidirectionalMappedSync(a: AT, b: BT, converter: Converter<A, B>): Registration
      where AT : ObservableValue<A>,
            AT : WritableObjectValue<A>,
            BT : ObservableValue<B>,
            BT : WritableObjectValue<B> {
    return bidirectionalMappedSync(a,b, converter.asChanging())
  }

  fun <A : Any, B : Any, AT, BT> bidirectionalMappedSync(a: AT, b: BT, converter: ChangingConverter<A, B>): Registration
      where AT : ObservableValue<A>,
            AT : WritableObjectValue<A>,
            BT : ObservableValue<B>,
            BT : WritableObjectValue<B> {
    val ignoreChanges = AtomicBoolean(false)
    val changeListenerA = ChangeListener<A> { observable, oldValue, newValue ->
      if (ignoreChanges.compareAndSet(false, true)) {
        b.set(converter.to(newValue, b.get()))
        ignoreChanges.set(false)
      }
    }
    val changeListenerB = ChangeListener<B> { observable, oldValue, newValue ->
      if (ignoreChanges.compareAndSet(false, true)) {
        a.set(converter.from(newValue, a.get()))
        ignoreChanges.set(false)
      }
    }
    a.addListener(changeListenerA)
    b.addListener(changeListenerB)
    return Registration {
      a.removeListener(changeListenerA)
      b.removeListener(changeListenerB)
    }
  }

  fun <A : Any, B : Any, AT> bidirectionalMappedSync(a: AT, converter: ChangingConverter<A, B>): RegisteredWritableObservableValue<B>
      where AT : ObservableValue<A>,
            AT : WritableObjectValue<A> {
    val b = SimpleObjectProperty<B>()
    val registration = bidirectionalMappedSync(a, b, converter)
    return RegisteredWritableObservableValue(b, registration)
  }

  fun <A : Any, B : Any, AT> bidirectionalMappedSync(a: AT, converter: Converter<A, B>): RegisteredWritableObservableValue<B>
      where AT : ObservableValue<A>,
            AT : WritableObjectValue<A> {
    return bidirectionalMappedSync(a, converter.asChanging())
  }

}