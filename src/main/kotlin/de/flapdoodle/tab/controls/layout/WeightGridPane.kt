package de.flapdoodle.tab.controls.layout

import de.flapdoodle.tab.extensions.constraint
import de.flapdoodle.tab.extensions.heightLimits
import de.flapdoodle.tab.extensions.widthLimits
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.css.CssMetaData
import javafx.css.SimpleStyleableDoubleProperty
import javafx.css.Styleable
import javafx.css.StyleablePropertyFactory
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.control.Control
import javafx.scene.control.SkinBase
import tornadofx.*

class WeightGridPane : Control() {

  init {
    addClass(Style.clazz)
    stylesheets += Style().base64URL.toExternalForm()
  }

  companion object {
    fun setPosition(node: Node, column: Int, row: Int) {
      node.constraint[GridMap.Pos::class] = GridMap.Pos(column, row)
    }

//    @JvmStatic
//    fun getClassCssMetaData(): MutableList<CssMetaData<out Styleable, *>?> {
//      return Skin.ALL.toMutableList()
//    }
  }

  private val skin = Skin(this)
  override fun createDefaultSkin() = skin

  fun setRowWeight(row: Int, weight: Double) {
    skin.setRowWeight(row, weight)
  }

  fun setColumnWeight(column: Int, weight: Double) {
    skin.setColumnWeight(column, weight)
  }

//  override fun getUserAgentStylesheet(): String {
//    //return Style().base64URL.toExternalForm()
//    return stylesheets.joinToString(separator = ";") + Style().base64URL.toExternalForm()
//  }

  public override fun getChildren(): ObservableList<Node> {
    return super.getChildren()
  }

//  public static {@literal List<CssMetaData<? extends Styleable, ?>>} getClassCssMetaData() {
//    return FACTORY.getCssMetaData();
//  }
//
//  {@literal @}Override
//  public {@literal List<CssMetaData<? extends Styleable, ?>>} getControlCssMetaData() {
//    return FACTORY.getCssMetaData();
//  }

  override fun getControlCssMetaData(): List<CssMetaData<out Styleable, *>> {
    return Skin.CONTROL_CSS_META_DATA
  }

  class Skin(
      private val control: WeightGridPane
  ) : SkinBase<WeightGridPane>(control) {

    companion object {
      internal val CSS_HSPACE_NAME = "weighted-grid-horizontal-space"
      internal val CSS_VSPACE_NAME = "weighted-grid-vertical-space"

      //      @JvmField
      private val FACTORY = StyleablePropertyFactory<WeightGridPane>(Control.getClassCssMetaData())

      //      @JvmField
      private val CSS_HSPACE: CssMetaData<WeightGridPane, Number> = FACTORY.createSizeCssMetaData(
          CSS_HSPACE_NAME,
          { it.skin.horizontalSpace },
          2.0)

      private val CSS_VSPACE: CssMetaData<WeightGridPane, Number> = FACTORY.createSizeCssMetaData(
          CSS_VSPACE_NAME,
          { it.skin.verticalSpace },
          2.0)

      //      @JvmStatic
      internal val CONTROL_CSS_META_DATA = (FACTORY.cssMetaData + CSS_HSPACE + CSS_VSPACE)
    }

    private var rowWeights = AutoArray.empty<Double>()
    private var columnWeights = AutoArray.empty<Double>()
    private var gridMap: GridMap<Node> = GridMap()

    private val horizontalSpace = SimpleStyleableDoubleProperty(CSS_HSPACE, this, "hspace");
    private val verticalSpace = SimpleStyleableDoubleProperty(CSS_VSPACE, this, "vspace");

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
            (it.constraint[GridMap.Pos::class] ?: GridMap.Pos(10, 10)) to it
          }.toMap())
    }

    private fun updateState() {
      control.requestLayout()
    }

    private fun verticalSpace(): Double = verticalSpace.value
    private fun horizontalSpace(): Double = horizontalSpace.value

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
      WeightedSize(columnWeights.get(index) ?: 1.0, min, max)
    }


    private fun rowSizes() = gridMap.mapRows { index, list ->
      val limits = list.map { it.heightLimits() }
      val min = limits.map { it.first }.max() ?: 0.0
      val max = Math.max(min, limits.map { it.second }.max() ?: Double.MAX_VALUE)

//      require(max >= min) { "invalid min/max for $list -> $min ? $max" }
      WeightedSize(columnWeights.get(index) ?: 1.0, min, max)
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

  class Style : tornadofx.Stylesheet() {
    companion object {
      val clazz by cssclass()
      val horizontalSpace by cssproperty<Double>(Skin.CSS_HSPACE_NAME)
      val verticalSpace by cssproperty<Double>(Skin.CSS_VSPACE_NAME)
    }

    init {
      clazz {
        horizontalSpace.value = 4.0
        verticalSpace.value = 4.0
      }
    }
  }
}