package de.flapdoodle.tab.observable

import de.flapdoodle.fx.annotations.KotlinCompilerFix_SAM_Helper

interface ChangeListener<T : Any> {
  fun changed(src: AObservable<T>, old: T, new: T)

  companion object {
    @KotlinCompilerFix_SAM_Helper
    inline operator fun <T : Any> invoke(crossinline delegate: (AObservable<T>, T, T) -> Unit): ChangeListener<T> {
      return object : ChangeListener<T> {
        override fun changed(src: AObservable<T>, old: T, new: T) {
          delegate(src, old, new)
        }
      }
    }
  }
}