package de.flapdoodle.tab.bindings

interface Converter<S,D> {
  fun to(s: S?): D?
  fun from(d: D?): S?

  companion object {
    operator fun <S,D> invoke(to: (S?) -> D?, from: (D?) -> S?): Converter<S, D> {
      return object : Converter<S,D> {
        override fun to(s: S?) = to.invoke(s)
        override fun from(d: D?) = from.invoke(d)
      }
    }
  }
}