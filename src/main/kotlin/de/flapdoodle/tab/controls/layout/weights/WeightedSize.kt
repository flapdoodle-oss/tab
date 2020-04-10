package de.flapdoodle.tab.controls.layout.weights

data class WeightedSize(
    val weight: Double,
    val min: Double,
    val max: Double
) {
  init {
    require(weight >= 0.0) { "invalid weight: $weight" }
    require(min >= 0.0) { "invalid min: $min" }
    require(max >= 0.0 && max >= min) { "invalid max: $max (min: $min)" }
  }

  companion object {
    fun distribute(space: Double, items: List<WeightedSize>): List<Double> {
      return if (items.isNotEmpty())
        distributeNonEmpty(space, items)
      else
        emptyList()
    }

    private fun distributeNonEmpty(space: Double, items: List<WeightedSize>): List<Double> {
      val minWidth = items.sumByDouble { it.min }
      val maxWidth = doubleMaxIfInfinite(items.sumByDouble { it.max })

      if (minWidth >= space) {
        return items.map { it.min }
      }
      if (maxWidth <= space) {
        return items.map { it.max }
      }

      val sumOfWeights = items.sumByDouble { it.weight }
      val sizedItems = items.map(::SizedItems)

      distribute(space, sumOfWeights, sizedItems)

      return sizedItems.map { it.size() }
    }

    private fun distribute(space: Double, sumOfWeights: Double, sizedItems: List<SizedItems>) {
      val itemsWithLimitsReached = sizedItems.count { it.limitReached() }

      sizedItems.forEach {
        if (!it.limitReached()) {
          val spaceNeeded = space * it.src.weight / sumOfWeights
          when {
            spaceNeeded <= it.src.min -> it.setSize(it.src.min, true)
            spaceNeeded >= it.src.max -> it.setSize(it.src.max, true)
            else -> it.setSize(spaceNeeded)
          }
        }
      }

      val newItemsWithLimitsReached = sizedItems.count { it.limitReached() }
      val anyLimitReached = itemsWithLimitsReached != newItemsWithLimitsReached

      if (anyLimitReached) {
        val spaceUsed = sizedItems.sumByDouble { if (it.limitReached()) it.size() else 0.0 }
        val spaceLeft = space - spaceUsed
        if (spaceLeft > 0.0 && sizedItems.any { !it.limitReached() }) {
          val leftSumOfWeights = sizedItems.sumByDouble { if (it.limitReached()) 0.0 else it.src.weight }
          distribute(spaceLeft, leftSumOfWeights, sizedItems)
        }
      }
    }

    private fun doubleMaxIfInfinite(value: Double): Double {
      return if (value.isInfinite()) Double.MAX_VALUE else value
    }

    private class SizedItems(
        val src: WeightedSize
    ) {
      private var size: Double = 0.0
      private var limitReached: Boolean = false

      fun limitReached() = limitReached

      fun size() = size
      fun setSize(size: Double, limitReached: Boolean = false) {
        this.size = size
        this.limitReached = limitReached
      }
    }
  }
}