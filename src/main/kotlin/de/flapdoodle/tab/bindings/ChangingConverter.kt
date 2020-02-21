package de.flapdoodle.tab.bindings

fun <S,D> Converter<S,D>.asChanging(): ChangingConverter<S, D> {
  val that = this
  return object : ChangingConverter<S,D> {
    override fun to(s: S?, old: D?) = that.to(s)
    override fun from(d: D?, old: S?) = that.from(d)
  }
}

interface ChangingConverter<S,D> {
  fun to(s: S?, old: D?): D?
  fun from(d: D?, old: S?): S?

  companion object {
    operator fun <S,D> invoke(to: (S?, D?) -> D?, from: (D?, S?) -> S?): ChangingConverter<S, D> {
      return object : ChangingConverter<S,D> {
        override fun to(s: S?, old: D?) = to.invoke(s,old)
        override fun from(d: D?, old: S?) = from.invoke(d,old)
      }
    }
  }
}