package de.flapdoodle.tab.controls.tables

import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.collections.WeakListChangeListener
import javafx.scene.control.Control
import javafx.scene.control.ScrollPane
import javafx.scene.control.SkinBase
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import tornadofx.*


class SmartTable<T : Any>(
    internal val rows: ObservableList<T>
) : Control() {

  private val columns: ObservableList<Column<T, out Any>> = FXCollections.observableArrayList()
  private val skin = SmartTableSkin(this)

  private val rowsChangeListener = ListChangeListener<T> { skin.rowsChanged() }
  private val columnsChangeListener = ListChangeListener<Column<T, out Any>> { skin.columnsChanged() }

  init {
    rows.addListener(WeakListChangeListener(rowsChangeListener))
    columns.addListener(columnsChangeListener)

    addClass(SmartTableStyles.smartTable)
  }

  override fun createDefaultSkin() = skin
  fun columns() = columns

  class SmartTableSkin<T : Any>(
      control: SmartTable<T>
  ) : SkinBase<SmartTable<T>>(control) {


    private val header = SmartHeader(control.columns)
    private val rowsPane = SmartRows(control.rows, header.headerColumns()).apply {
      VBox.setVgrow(this, Priority.ALWAYS)
    }

    private val all = VBox().apply {
      add(header)
      add(rowsPane)
    }
    private val scrollPane = ScrollPane().apply {
      add(all)
    }

    init {
      children.add(scrollPane)
      columnsChanged()
      rowsChanged()
    }

    internal fun rowsChanged() {
      rowsPane.rowsChanged()
    }

    internal fun columnsChanged() {
      header.columnsChanged()
      rowsPane.columnsChanged()
    }
  }

}