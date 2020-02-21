package de.flapdoodle.tab.bindings

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.beans.value.WritableObjectValue

class MappingListener<S, D, DT>(
    private val converter: (S?, D?) -> D?,
    private val dst: DT
) : ChangeListener<S>
    where  DT : ObservableValue<D>,
           DT : WritableObjectValue<D> {
  override fun changed(observable: ObservableValue<out S>?, oldValue: S, newValue: S) {
    dst.set(converter(newValue, dst.get()))
  }

  companion object {
    fun <A : Any, B : Any, BT> convertToListener(
        converter: ChangingConverter<A, B>,
        dst: BT
    ): ChangeListener<A>
        where BT : ObservableValue<B>,
              BT : WritableObjectValue<B> {
      return MappingListener(
          converter = converter::to,
          dst = dst
      )
    }

    fun <A : Any, B : Any, AT> convertFromListener(
        converter: ChangingConverter<A, B>,
        dst: AT
    ): ChangeListener<B>
        where AT : ObservableValue<A>,
              AT : WritableObjectValue<A> {
      return MappingListener(
          converter = converter::from,
          dst = dst
      )
    }

  }
}