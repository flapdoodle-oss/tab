package de.flapdoodle.tab.controls.tables

import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.control.Skin
import javafx.scene.control.SkinBase
import javafx.scene.layout.GridPane
import tornadofx.*

class SmartTable<T : Any>(
    internal val rows: ObservableList<T>
) : Control() {

  private val columns: ObservableList<Column<out Any, T>> = FXCollections.observableArrayList()
  private val skin = SmartTableSkin(this)

  override fun createDefaultSkin() = skin
  fun columns() = columns

  class SmartTableSkin<T : Any>(
      private val table: SmartTable<T>
  ) : SkinBase<SmartTable<T>>(table) {
    private val grid = GridPane()

    init {
      children.add(grid)
      table.columns.addListener(ListChangeListener {
        grid.removeAllRows()
        it.list.forEachIndexed { index, column ->
          grid.add(Label("$index"),index,0)
        }
      })
    }
  }
}