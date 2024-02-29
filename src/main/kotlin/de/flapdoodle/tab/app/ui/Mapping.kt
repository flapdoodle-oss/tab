package de.flapdoodle.tab.app.ui

class Mapping<K, R, V> {
  private var keyMap: Map<K, R> = emptyMap()
  private var reverseMap: Map<R, K> = emptyMap()
  private var map: Map<K, V> = emptyMap()

  fun add(key: K, reverseKey: R, value: V) {
    val oldReverseKey = keyMap[key]
    val oldKey = reverseMap[reverseKey]
    val oldValue = map[key]
    require(oldReverseKey == null) { "reverseKey already set to $oldReverseKey" }
    require(oldKey == null) { "key already set to $oldKey" }
    require(oldValue == null) { "value already set to $oldValue" }

    keyMap = keyMap + (key to reverseKey)
    reverseMap = reverseMap + (reverseKey to key)
    map = map + (key to value)
  }

  fun reverseKey(key: K): R? {
    return keyMap[key]
  }

  operator fun get(key: K): V? {
    return map[key]
  }

  fun with(key: K, onValue: (V) -> Unit) {
    val value = requireNotNull(get(key)) { "could not get value for $key" }
    onValue(value)
  }

  fun key(reverseKey: R): K? {
    return reverseMap[reverseKey]
  }

  fun remove(key: K, onValue: (V) -> Unit = {}) {
    val reverseKey = requireNotNull(keyMap[key]) { "could not find reverse key" }
    val value = requireNotNull(get(key)) { "could not get value for $key" }
    keyMap = keyMap - key
    reverseMap = reverseMap - reverseKey
    map = map - key
    onValue(value)
  }
}