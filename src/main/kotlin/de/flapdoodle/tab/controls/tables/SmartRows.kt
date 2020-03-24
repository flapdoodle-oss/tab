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

  internal fun setCursor(cursor: Cursor<T>) {
    skin.setCursor(cursor)
  }

  class SmartRowsSkin<T : Any>(
      private val control: SmartRows<T>
  ) : SkinBase<SmartRows<T>>(control) {
    private val rowPane = VBox()

    init {
      // TODO what?
      consumeMouseEvents(false)

      children.add(rowPane)

      rowsChanged()
    }

    internal fun rowsChanged() {
      rowPane.children.setAll(control.rows.mapIndexed { index, t ->
        SmartRow(control.columns, t, index)
      })
    }

    fun columnsChanged() {
      rowPane.children.forEach {
        (it as SmartRow<T>).columnsChanged()
      }
    }

    internal fun setCursor(cursor: Cursor<T>) {
      rowPane.children.forEach {
        (it as SmartRow<T>).setCursor(cursor)
      }
    }
  }
}