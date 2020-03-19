package de.flapdoodle.tab.controls.tables

import javafx.collections.ObservableList
import javafx.scene.control.Control
import javafx.scene.control.SkinBase
import javafx.scene.layout.VBox
import tornadofx.*

class SmartRows<T : Any>(
    private val rows: ObservableList<T>,
    private val columns: ObservableList<out SmartColumn<T, out Any>>
) : Control() {

  private val skin = SmartRowsSkin(this)

  init {
    addClass(SmartTableStyles.smartRows)
  }

  override fun createDefaultSkin() = skin
  internal fun rowsChanged() {
    skin.rowsChanged()
  }

  fun columnsChanged() {
    skin.columnsChanged()
  }

  class SmartRowsSkin<T : Any>(
      private val control: SmartRows<T>
  ) : SkinBase<SmartRows<T>>(control) {
    private val rowPane = VBox()

    init {
      children.add(rowPane)

      rowsChanged()
    }

    internal fun rowsChanged() {
      rowPane.children.setAll(control.rows.mapIndexed { index, t ->
        SmartRow(control.columns, t, index % 2 == 0)
      })
//      rowPane.children.clear()
//      control.rows.forEachIndexed { index, it ->
//        rowPane.add(SmartRow(control.columns, it, index % 2 == 0))
//      }
    }

    fun columnsChanged() {
      rowPane.children.forEach {
        (it as SmartRow<T>).columnsChanged()
      }
    }
  }
}