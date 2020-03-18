package de.flapdoodle.tab.controls.tables

import javafx.scene.Node
import kotlin.reflect.KClass

data class Column<T : Any, C: Any>(
    val type: KClass<C>,
    val columnName: String,
    val valueFactory: (T) -> C?
) {
  companion object {
    inline fun <T: Any, reified C : Any> of(name: String, noinline valueFactory: (T) -> C?): Column<T, C> {
      return Column(C::class, name, valueFactory)
    }
  }
}