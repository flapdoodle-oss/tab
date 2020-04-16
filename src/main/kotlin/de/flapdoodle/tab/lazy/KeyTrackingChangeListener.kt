package de.flapdoodle.tab.lazy

import javafx.collections.ObservableList

class KeyTrackingChangeListener<S: Any, K: Any, M: Any, D: Any>(
    private val src: LazyValue<List<S>>,
    private val children: ObservableList<D>,
    private val keyOf: (source: S) -> K,
    private val extract: (M) -> List<D>,
    private val map: (entry: PositionedEntry<S>, old: M?) -> M
) : ChangedListener<List<S>> {
  private var start = map(PositionedEntry.Start(), null)
  private var mapped = src.value().mapIndexed { index, it -> keyOf(it) to map(PositionedEntry.WithIndex(index, it), null) }
  private var end = map(PositionedEntry.End(src.value().size), null)

  init {
    children.addAll(mapped.flatMap { extract(it.second) })
  }

  override fun hasChanged(value: LazyValue<List<S>>) {
    val new = src.value()

    val current = mapped

    val newStart = map(PositionedEntry.Start(), start)
    val merged = new.mapIndexed { index, source ->
      val key = keyOf(source)
      val alreadyMapped = current.find { it.first == key }
      key to map(PositionedEntry.WithIndex(index, source), alreadyMapped?.second)
    }
    val newEnd = map(PositionedEntry.End(new.size), end)

    val mergedKeys = merged.map { it.first }.toSet()
    val removed = current.filter { !mergedKeys.contains(it.first) }
    children.removeAll(removed.flatMap { extract(it.second) })

    val currentChildren = extract(newStart) + merged.flatMap { extract(it.second) } + extract(newEnd)

    children.removeAll(currentChildren)
    children.addAll(currentChildren)

    start = newStart
    mapped = merged
    end = newEnd
  }
}