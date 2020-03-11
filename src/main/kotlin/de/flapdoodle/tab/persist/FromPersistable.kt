package de.flapdoodle.tab.persist

import de.flapdoodle.tab.annotations.KotlinCompilerFix_SAM_Helper

interface FromPersistable<T: Any, S: Any> {
  fun from(context: FromPersistableContext, source: S): T

  companion object {
    @KotlinCompilerFix_SAM_Helper
    inline operator fun <T: Any, S: Any> invoke(crossinline delegate: (FromPersistableContext, S) -> T): FromPersistable<T,S> {
      return object : FromPersistable<T,S> {
        override fun from(context: FromPersistableContext, source: S): T {
          return delegate(context,source)
        }
      }
    }
  }
}