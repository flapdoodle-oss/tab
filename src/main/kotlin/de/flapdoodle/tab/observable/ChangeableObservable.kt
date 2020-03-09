package de.flapdoodle.tab.observable

class ChangeableObservable<T : Any>(
    initialValue: T
) : Changeable<T>, AObservable<T> {

  private var current = initialValue
  private var changeListener: List<ChangeListener<T>> = emptyList()

  override fun value(value: T) {
    val old = current
    current = value
    changeListener.forEach {
      it.changed(this, old, current)
    }
  }

  override fun value() = current

  override fun addListener(listener: ChangeListener<T>) {
    changeListener = changeListener + listener
  }

  override fun removeListener(listener: ChangeListener<T>) {
    changeListener = changeListener - listener
  }
}