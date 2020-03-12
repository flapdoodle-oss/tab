package de.flapdoodle.tab.data.values

import kotlin.reflect.KClass

sealed class Input<T: Any> {
  abstract val type: KClass<T>
  abstract val name: String

  data class Variable<T: Any>(
      override val type: KClass<T>,
      override val name: String
  ): Input<T>()

  data class List<T: Any>(
      override val type: KClass<T>,
      override val name: String
  ): Input<T>()
}
