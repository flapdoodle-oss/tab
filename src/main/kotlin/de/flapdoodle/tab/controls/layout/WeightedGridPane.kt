package de.flapdoodle.tab.controls.layout

import de.flapdoodle.tab.extensions.heightLimits
import de.flapdoodle.tab.extensions.widthLimits
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.control.Control
import javafx.scene.control.SkinBase

class WeightedGridPane() : Control() {

  private val skin = Skin(this)

  override fun createDefaultSkin() = skin

  fun add(
      node: Node,
      column: Int,
      row: Int
  ) {
    skin.add(node, column, row)
  }

  fun setRowWeight(row: Int, weight: Double) {
    skin.setRowWeight(row, weight)
  }

  fun setColumnWeight(column: Int, weight: Double) {
    skin.setColumnWeight(column, weight)
  }

  class Skin(
      private val control: WeightedGridPane
  ) : SkinBase<WeightedGridPane>(control) {

    private var gridMap: GridMap<Node> = GridMap()
    private var rowWeights = AutoArray.empty<Double>()
    private var columnWeights = AutoArray.empty<Double>()

//    private var columnSizes: List<WeightedSize> = emptyList()
//    private var rowSizes: List<WeightedSize> = emptyList()

//    private var minColumnWidth: List<Double> = emptyList()
//    private var minRowHeight: List<Double> = emptyList()

//    private var rowWeightSum = 0
//    private var columnWeightSum = 0

    private fun updateState() {
      children.setAll(gridMap.values())

//      columnSizes = gridMap.mapColumns { index, list ->
//        val limits = list.map { it.widthLimits() }
//        val min = limits.map { it.first }.max() ?: 0.0
//        val max = limits.map { it.second }.min() ?: Double.MAX_VALUE
//
//        require(max >= min) {"invalid min/max for $list -> $min ? $max"}
//        WeightedSize(columnWeights.get(index) ?: 1.0, min, max)
//      }
//
//      rowSizes = gridMap.mapRows { index, list ->
//        val limits = list.map { it.heightLimits() }
//        val min = limits.map { it.first }.max() ?: 0.0
//        val max = limits.map { it.second }.min() ?: Double.MAX_VALUE
//
//        require(max >= min) {"invalid min/max for $list -> $min ? $max"}
//        WeightedSize(columnWeights.get(index) ?: 1.0, min, max)
//      }

//      minColumnWidth = gridMap.mapColumns { list ->
//        list.map { it.minWidth(-1.0) }.max() ?: 0.0
//      }
//      minRowHeight = gridMap.mapRows { list ->
//        list.map { it.minHeight(-1.0) }.max() ?: 0.0
//      }

//      rowWeightSum = rowWeights.mapNotNull { it }.sum()
//
//      columnWeightSum = columnWeights.mapNotNull { it }.sum()

      control.requestLayout()
    }

    private fun columnSizes() = gridMap.mapColumns { index, list ->
      val limits = list.map { it.widthLimits() }
      val min = limits.map { it.first }.max() ?: 0.0
      val max = limits.map { it.second }.min() ?: Double.MAX_VALUE

      require(max >= min) { "invalid min/max for $list -> $min ? $max" }
      WeightedSize(columnWeights.get(index) ?: 1.0, min, max)
    }


    private fun rowSizes() = gridMap.mapRows { index, list ->
      val limits = list.map { it.heightLimits() }
      val min = limits.map { it.first }.max() ?: 0.0
      val max = limits.map { it.second }.min() ?: Double.MAX_VALUE

      require(max >= min) { "invalid min/max for $list -> $min ? $max" }
      WeightedSize(columnWeights.get(index) ?: 1.0, min, max)
    }


    fun add(
        node: Node,
        column: Int,
        row: Int
    ) {
      require(row >= 0) { "invalid row: $row" }
      require(column >= 0) { "invalid column: $column" }

      gridMap = gridMap.add(GridMap.Pos(column, row), node)

      updateState()
    }

    fun setRowWeight(row: Int, weight: Double) {
      require(row >= 0) { "invalid row: $row" }
      require(weight >= 0.0) { "invalid weight: $weight" }

      rowWeights = rowWeights.set(row, weight)

      updateState()
    }

    fun setColumnWeight(column: Int, weight: Double) {
      require(column >= 0) { "invalid column: $column" }
      require(weight >= 0.0) { "invalid weight: $weight" }

      columnWeights = columnWeights.set(column, weight)

      updateState()
    }

    override fun computeMinWidth(height: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double): Double {
      val ret = columnSizes().sumByDouble { it.min }
      return ret
    }

    override fun computeMinHeight(width: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double): Double {
      val ret = rowSizes().sumByDouble { it.min }
      return ret
    }

    override fun computePrefWidth(height: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double): Double {
      return gridMap.mapColumns { _, list ->
        list.map { it.prefWidth( -1.0) }.max() ?: 0.0
      }.sumByDouble { it }
    }

    override fun computePrefHeight(width: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double): Double {
      return gridMap.mapRows { _, list ->
        list.map { it.prefHeight( -1.0) }.max() ?: 0.0
      }.sumByDouble { it }
    }

    override fun layoutChildren(contentX: Double, contentY: Double, contentWidth: Double, contentHeight: Double) {
//      println("-------------------------")
      val columnSizes = columnSizes()
      val rowSizes = rowSizes()

//      println("columns")
//      columnSizes.forEach { println(it) }
//      println("rows")
//      rowSizes.forEach { println(it) }

      val colWidths = WeightedSize.distribute(contentWidth, columnSizes)
      val rowHeights = WeightedSize.distribute(contentHeight, rowSizes)

//      println("widths: $colWidths")
//      println("heights: $rowHeights")
//      println("-------------------------")

      gridMap.rows().forEach { r ->
        gridMap.columns().forEach { c ->
          val node = gridMap[GridMap.Pos(c, r)]
          if (node != null) {
            val areaX = colWidths.subList(0, c).sumByDouble { it }
            val areaY = rowHeights.subList(0, r).sumByDouble { it }

            val areaW = colWidths[c]
            val areaH = rowHeights[r]

            layoutInArea(node, areaX, areaY, areaW, areaH, -1.0, HPos.CENTER, VPos.CENTER)
          }
        }
      }
    }
  }
}