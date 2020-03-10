package de.flapdoodle.tab.lazy

import de.flapdoodle.tab.annotations.KotlinCompilerFix_SAM_Helper

interface ChangedListener<T: Any> {
  fun hasChanged(value: LazyValue<T>)

  companion object {
    @KotlinCompilerFix_SAM_Helper
    inline operator fun <T : Any> invoke(crossinline delegate: (LazyValue<T>) -> Unit): ChangedListener<T> {
      return object : ChangedListener<T> {
        override fun hasChanged(value: LazyValue<T>) {
          delegate(value)
        }
      }
    }
  }

}