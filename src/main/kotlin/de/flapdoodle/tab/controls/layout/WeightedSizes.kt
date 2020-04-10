package de.flapdoodle.tab.controls.layout

object WeightedSizes {

  fun calculate(size: Double, items: List<Limit>, weights: (index: Int) -> Int): List<Double> {
    return if (items.isNotEmpty())
      calculateNonEmpty(size, items, weights)
    else
      emptyList()
  }

  private fun calculateNonEmpty(size: Double, items: List<Limit>, weights: (index: Int) -> Int): List<Double> {
    // alle mit weight = 0 bleiben auf min-size
    // wunschgröße schritt 1
    // alle mit max < wunschgröße auf max
    //


    val left = size - items.sumByDouble { it.min }

    return if (left >= 0.0) {
      val allWeights = (0..items.size).map(weights)
      val weightSum = allWeights.sum()
      if (weightSum > 0) {
        val wishSize = allWeights.map { (size * it) / weightSum }
      } else {
        // nothing to stretch
      }
      items.map { it.min }
    } else {
      items.map { it.min }
    }
  }

}