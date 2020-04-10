package de.flapdoodle.tab.controls.layout

data class Limit(
    val min: Double,
    val max: Double
) {
  init {
    require(min >= 0.0) { "invalid min: $min" }
    require(max >= 0.0 && max >= min) { "invalid max: $max (min: $min)" }
  }

  operator fun plus(other: Limit): Limit {
    return Limit(addOrMax(min, other.min), addOrMax(max, other.max))
  }

  companion object {

    private fun addOrMax(a: Double, b: Double): Double {
      val ret = a + b

      return if (ret.isInfinite())
        Double.MAX_VALUE
      else
        ret
    }

    fun sum(src: Collection<Limit>): Limit {
      return src.fold(Limit(0.0, 0.0)) { a, b ->
        val min = a.min + b.min
        val max = a.max + b.max
        Limit(min, max)
      }
    }
  }
}