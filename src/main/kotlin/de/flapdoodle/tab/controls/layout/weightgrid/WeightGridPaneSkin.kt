package de.flapdoodle.tab.controls.layout.weightgrid

import de.flapdoodle.tab.controls.layout.AutoArray
import de.flapdoodle.tab.controls.layout.GridMap
import de.flapdoodle.tab.controls.layout.WeightedSize
import de.flapdoodle.tab.extensions.constraint
import de.flapdoodle.tab.extensions.heightLimits
import de.flapdoodle.tab.extensions.widthLimits
import javafx.collections.ListChangeListener
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.control.SkinBase

class WeightGridPaneSkin(
    private val control: WeightGridPane
) : SkinBase<WeightGridPane>(control) {

//  private var rowWeights = AutoArray.empty<Double>()
//  private var columnWeights = AutoArray.empty<Double>()
  private var gridMap: GridMap<Node> = GridMap()

  init {
    children.addListener(ListChangeListener {
      gridMap = gridMap()
      updateState()
    })

    control.needsLayoutProperty().addListener { observable, oldValue, newValue ->
      gridMap = gridMap()
    }
  }

  private fun gridMap(): GridMap<Node> {
    return GridMap(children
        .filter { it.isManaged }
        .map { it: Node ->
          (it.constraint[GridMap.Pos::class]
              ?: GridMap.Pos(10, 10)) to it
        }.toMap())
  }

  private fun updateState() {
    control.requestLayout()
  }

  private fun verticalSpace(): Double = control.verticalSpace.value
  private fun horizontalSpace(): Double = control.horizontalSpace.value

  private fun <T : Any> List<T>.sumWithSpaceBetween(space: Double, selector: (T) -> Double): Double {
    return sumByDouble(selector) + if (isEmpty()) 0.0 else (size - 1) * space
  }

  private fun <T : Any> List<T>.sumWithSpaceAfter(space: Double, selector: (T) -> Double): Double {
    return sumByDouble(selector) + size * space
  }

  private fun columnSizes() = gridMap.mapColumns { index, list ->
    val limits = list.map { it.widthLimits() }
    val min = limits.map { it.first }.max() ?: 0.0
    val max = Math.max(min, limits.map { it.second }.max() ?: Double.MAX_VALUE)

//      require(max >= min) { "invalid min/max for $list -> $min ? $max" }
    WeightedSize(control.columnWeights.get(index) ?: 1.0, min, max)
  }


  private fun rowSizes() = gridMap.mapRows { index, list ->
    val limits = list.map { it.heightLimits() }
    val min = limits.map { it.first }.max() ?: 0.0
    val max = Math.max(min, limits.map { it.second }.max() ?: Double.MAX_VALUE)

//      require(max >= min) { "invalid min/max for $list -> $min ? $max" }
    WeightedSize(control.columnWeights.get(index) ?: 1.0, min, max)
  }

  override fun computeMinWidth(height: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double): Double {
    val width = columnSizes().sumWithSpaceBetween(horizontalSpace()) { it.min }
    return width + leftInset + rightInset
  }

  override fun computeMinHeight(width: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double): Double {
    val ret = rowSizes().sumWithSpaceBetween(verticalSpace()) { it.min }
    return ret + topInset + bottomInset
  }

  override fun computePrefWidth(height: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double): Double {
    val ret = gridMap.mapColumns { _, list ->
      list.map { it.prefWidth(-1.0) }.max() ?: 0.0
    }.sumWithSpaceBetween(horizontalSpace()) { it }
    return ret + leftInset + rightInset
  }

  override fun computePrefHeight(width: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double): Double {
    val ret = gridMap.mapRows { _, list ->
      list.map { it.prefHeight(-1.0) }.max() ?: 0.0
    }.sumWithSpaceBetween(verticalSpace()) { it }
    return ret + topInset + bottomInset
  }

  override fun layoutChildren(contentX: Double, contentY: Double, contentWidth: Double, contentHeight: Double) {
//      println("-------------------------")

//      println("hspace: ${horizontalSpace.value}")
    val columnSizes = columnSizes()
    val rowSizes = rowSizes()

    val hSpaces = if (columnSizes.isEmpty()) 0.0 else (columnSizes.size-1) * horizontalSpace()
    val vSpaces = if (rowSizes.isEmpty()) 0.0 else (rowSizes.size-1) * verticalSpace()

//      println("columns")
//      columnSizes.forEach { println(it) }
//      println("rows")
//      rowSizes.forEach { println(it) }

    val colWidths = WeightedSize.distribute(contentWidth - hSpaces, columnSizes)
    val rowHeights = WeightedSize.distribute(contentHeight - vSpaces, rowSizes)

//      println("widths: $colWidths")
//      println("heights: $rowHeights")
//      println("-------------------------")

    gridMap.rows().forEach { r ->
      gridMap.columns().forEach { c ->
        val node = gridMap[GridMap.Pos(c, r)]
        if (node != null && node.isManaged) {
          val areaX = contentX + colWidths.subList(0, c).sumWithSpaceAfter(horizontalSpace()) { it }
          val areaY = contentY + rowHeights.subList(0, r).sumWithSpaceAfter(verticalSpace()) { it }

          val areaW = colWidths[c]
          val areaH = rowHeights[r]

          layoutInArea(node, areaX, areaY, areaW, areaH, -1.0, HPos.CENTER, VPos.CENTER)
        }
      }
    }
  }
}