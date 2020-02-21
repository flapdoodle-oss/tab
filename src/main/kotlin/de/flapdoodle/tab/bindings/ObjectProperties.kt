package de.flapdoodle.tab.bindings

import de.flapdoodle.tab.fx.SingleThreadMutex
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.beans.value.WritableObjectValue

fun <A : Any, B : Any, AT> AT.mapped(converter: Converter<A, B>): Pair<Registration, WritableObservableValue<B, *>>
    where AT : ObservableValue<A>,
          AT : WritableObjectValue<A> {
  return ObjectProperties.bidirectionalMappedSync(this, converter)
}

object ObjectProperties {

  fun <A : Any, B : Any, AT, BT> bidirectionalMappedSync(a: AT, b: BT, converter: ChangingConverter<A, B>): Registration
      where AT : ObservableValue<A>,
            AT : WritableObjectValue<A>,
            BT : ObservableValue<B>,
            BT : WritableObjectValue<B> {
    val mutex = SingleThreadMutex()

    val changeListenerA = MappingListener.convertToListener(converter,b).tryExecuteIn(mutex)
    val changeListenerB = MappingListener.convertFromListener(converter, a).tryExecuteIn(mutex)

    a.addListener(changeListenerA)
    b.addListener(changeListenerB)

    mutex.execute {
      b.set(converter.to(a.get(),b.get()))
    }

    return Registration {
      a.removeListener(changeListenerA)
      b.removeListener(changeListenerB)
    }
  }

  fun <A : Any, B : Any, AT> bidirectionalMappedSync(a: AT, converter: ChangingConverter<A, B>): Pair<Registration, WritableObservableValue<B, *>>
      where AT : ObservableValue<A>,
            AT : WritableObjectValue<A> {
    val b = SimpleObjectProperty<B>()
    val registration = bidirectionalMappedSync(a, b, converter)
    return registration to WritableObservableValue(b)
  }

  fun <A : Any, B : Any, AT, BT> bidirectionalMappedSync(a: AT, b: BT, converter: Converter<A, B>): Registration
      where AT : ObservableValue<A>,
            AT : WritableObjectValue<A>,
            BT : ObservableValue<B>,
            BT : WritableObjectValue<B> {
    return bidirectionalMappedSync(a, b, converter.asChanging())
  }

  fun <A : Any, B : Any, AT> bidirectionalMappedSync(a: AT, converter: Converter<A, B>): Pair<Registration, WritableObservableValue<B, *>>
      where AT : ObservableValue<A>,
            AT : WritableObjectValue<A> {
    return bidirectionalMappedSync(a, converter.asChanging())
  }
}