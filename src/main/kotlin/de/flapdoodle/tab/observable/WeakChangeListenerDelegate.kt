package de.flapdoodle.tab.observable

import java.lang.ref.WeakReference

class WeakChangeListenerDelegate<T: Any>(
  delegate: ChangeListener<T>
) : ChangeListener<T> {

  private val weakReference = WeakReference(delegate)

  override fun changed(src: AObservable<T>, old: T, new: T) {
    val org = weakReference.get()
    if (org!=null) {
      org.changed(src, old, new)
    } else {
      src.removeListener(this)
    }
  }
}