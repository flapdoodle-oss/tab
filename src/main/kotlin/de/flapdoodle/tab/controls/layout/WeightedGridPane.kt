package de.flapdoodle.tab.controls.layout

import de.flapdoodle.tab.extensions.Key
import de.flapdoodle.tab.extensions.property
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.control.Control
import javafx.scene.control.SkinBase
import java.lang.Integer.max

class WeightedGridPane() : Control() {

  private val skin = Skin(this)

  override fun createDefaultSkin() = skin

  fun add(
      node: Node,
      column: Int,
      row: Int
  ) {
    require(row >= 0) { "invalid row: $row" }
    require(column >= 0) { "invalid column: $column" }

    skin.nodes.add(node.apply {
      property[Pos.KEY] = Pos(column, row)

      property[Key.ofType(GridMap.Pos::class)] = GridMap.Pos(column, row)
    })
  }

  fun setRowWeight(row: Int, weight: Int) {
    require(row >= 0) { "invalid row: $row" }
    skin.rowWeights.set(row, weight)
  }

  fun setColumnWeight(column: Int, weight: Int) {
    require(column >= 0) { "invalid column: $column" }
    skin.columnWeights.set(column, weight)
  }

  class Skin(
      private val control: WeightedGridPane
  ) : SkinBase<WeightedGridPane>(control) {

    internal val nodes = FXCollections.observableArrayList<Node>()
    internal val rowWeights = FXCollections.observableArrayList<Int>()
    internal val columnWeights = FXCollections.observableArrayList<Int>()

    private var gridMap: GridMap<Node> = GridMap()
    private var minColumnWidth: List<Double> = emptyList()
    private var minRowHeight: List<Double> = emptyList()

    private var rowWeightSum = 0
    private var columnWeightSum = 0

    @Deprecated("dont use")
    private var positionMap: Map<Pos, Node> = emptyMap()

    init {
      nodes.addListener(ListChangeListener {
        positionMap = nodes.map { it.property[Pos.KEY]!! to it }.toMap()


        gridMap = GridMap.create(nodes) { it.property[Key.ofType(GridMap.Pos::class)]!! }
        minColumnWidth = gridMap.mapColumns { list ->
          list.map { it.minWidth(-1.0) }.max() ?: 0.0
        }
        minRowHeight = gridMap.mapRows { list ->
          list.map { it.minHeight(-1.0) }.max() ?: 0.0
        }
        children.setAll(nodes)

        control.requestLayout()
      })

      rowWeights.addListener(ListChangeListener {
        rowWeightSum = rowWeights.mapNotNull { it }.sum()

        control.requestLayout()
      })

      columnWeights.addListener(ListChangeListener {
        columnWeightSum = columnWeights.mapNotNull { it }.sum()

        control.requestLayout()
      })
    }

    override fun computeMinWidth(height: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double): Double {
      return minColumnWidth.sum()
    }

    override fun computeMinHeight(width: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double): Double {
      return minRowHeight.sum()
    }

//    override fun computePrefWidth(height: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double): Double {
//      return 10.0
//    }
//
//    override fun computePrefHeight(width: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double): Double {
//      return 10.0
//    }

    override fun layoutChildren(contentX: Double, contentY: Double, contentWidth: Double, contentHeight: Double) {
      val unusedWidth = contentWidth - minColumnWidth.sum()
      val unusedHeight = contentHeight - minRowHeight.sum()

      gridMap.columns().map {
        //columnWeights.find
      }

      val maxPos = positionMap.keys.fold(Pos(0, 0), Pos::max)

      val colWidth = contentWidth / (maxPos.column + 1)
      val rowHeight = contentHeight / (maxPos.row + 1)

      (0..maxPos.row).forEach { r ->
        (0..maxPos.column).forEach { c ->
          val node = positionMap[Pos(c, r)]
          if (node != null) {
            val areaX = c * colWidth
            val areaY = r * rowHeight
            val areaW = colWidth
            val areaH = rowHeight

            layoutInArea(node, areaX, areaY, areaW, areaH, -1.0, HPos.CENTER, VPos.CENTER)
          }
        }
      }
    }
  }

  data class Pos(val column: Int, val row: Int) {
    companion object {
      internal val KEY = Key.ofType(Pos::class)
    }

    fun max(other: Pos): Pos {
      return Pos(max(column, other.column), max(row, other.row))
    }
  }


}