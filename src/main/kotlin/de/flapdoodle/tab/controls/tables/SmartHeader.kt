package de.flapdoodle.tab.controls.tables

import de.flapdoodle.tab.controls.layout.BetterSplitPane
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.control.SkinBase
import javafx.scene.control.SplitPane
import tornadofx.*

class SmartHeader<T : Any>(
    private val columns: ObservableList<out SmartColumn<T, out Any>>
) : Control() {

  private val skin = SmartHeaderSkin(this)

  init {
    addClass(SmartTableStyles.smartHeader)
  }

  internal fun columnsChanged() {
    skin.columnsChanged()
  }

  override fun createDefaultSkin() = skin

  class SmartHeaderSkin<T : Any>(
      private val src: SmartHeader<T>
  ) : SkinBase<SmartHeader<T>>(src) {
    private val header = BetterSplitPane().apply {
//      addClass(TabStyle.styledSplitPane)
//      prefWidth = 200.0
    }

//    private val headerColumns = FXCollections.observableArrayList<ColumnHeader<T>>()

    internal fun columnsChanged() {
//      header.items.bind(src.columns) { SmartHeaderColumn(it) }
      header.nodes().setAll(src.columns/* + Label("   ").apply { maxWidth = Double.MAX_VALUE }*/)

//      header.nodes().addListener(ListChangeListener {
//        val size = it.list.size
//        if (size > 0) {
//          val offset = 1.0 / (size + 1)
//          (0 until size).forEach { index ->
//            if (false) {
//              header.setDividerPosition(index, offset * (index + 1))
//            }
//          }
//        }
//      })

//      headerColumns.setAll(src.columns.map { ColumnHeader(it) })
//      header.items.setAll(headerColumns)
//      if (headerColumns.isNotEmpty()) {
//        val offset = 1.0 / headerColumns.size
//        headerColumns.forEachIndexed { index, _ ->
//          header.setDividerPosition(index, offset * (index + 1))
//        }
//      }
    }

    init {
      children.add(header)
//      columnsChanged()
    }
  }

}