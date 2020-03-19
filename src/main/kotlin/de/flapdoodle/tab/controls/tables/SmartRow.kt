package de.flapdoodle.tab.controls.tables

import javafx.collections.ObservableList
import javafx.scene.control.Control
import javafx.scene.control.Skin
import javafx.scene.control.SkinBase
import javafx.scene.layout.HBox
import tornadofx.*

class SmartRow<T : Any>(
    internal val columns: ObservableList<out SmartColumn<T, out Any>>,
    internal val value: T,
    internal val even: Boolean
) : Control() {

  private val skin = SmartRowSkin(this)

  init {
    addClass(SmartTableStyles.smartRow)
    if (even) {
      addClass(Stylesheet.even)
    } else {
      addClass(Stylesheet.odd)
    }
  }

  override fun createDefaultSkin(): Skin<*> {
    return skin
  }

  fun columnsChanged() {
    skin.columnsChanged()
  }


  class SmartRowSkin<T : Any>(
      private val row: SmartRow<T>
  ) : SkinBase<SmartRow<T>>(row) {
    private val rowContainer = HBox()

    fun columnsChanged() {
      rowContainer.children.setAll(row.columns.map { cell(it, row.value) })
    }

    init {
      children.add(rowContainer)
      columnsChanged()
    }

    private fun <T : Any, C : Any> cell(c: SmartColumn<T, C>, row: T): SmartCell<T, C> {
      return c.cell(row).apply {
        prefWidthProperty().bind(c.widthProperty())
      }

//      return object : SmartCell<T,C>(
//          value = c.column.valueFactory(row),
//          editable = c.column.onChange!=null,
//          converter = c.converter()
//      ) {
//        override fun onChange(value: C?) {
//          c.column.onChange?.let {
//            it(row,value)
//          }
//        }
//      }.apply {
//        prefWidthProperty().bind(c.widthProperty())
//      }
    }
  }

}