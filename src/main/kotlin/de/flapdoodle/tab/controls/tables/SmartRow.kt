package de.flapdoodle.tab.controls.tables

import javafx.collections.ObservableList
import javafx.scene.control.Control
import javafx.scene.control.Skin
import javafx.scene.control.SkinBase
import javafx.scene.layout.HBox
import tornadofx.*

class SmartRow<T : Any>(
    internal val columns: ObservableList<out SmartColumn<T, out Any>>,
    internal val value: T
) : Control() {

  private val skin = SmartRowSkin(this)

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
      rowContainer.children.clear()
      row.columns.forEach {
        rowContainer.add(cell(it, row.value))
      }
    }

    init {
      children.add(rowContainer)
    }

    private fun <T: Any, C: Any> cell(c: SmartColumn<T,C>, value: T): SmartCell<C> {
      return SmartCell(c, c.column.valueFactory(value))
    }
  }

}