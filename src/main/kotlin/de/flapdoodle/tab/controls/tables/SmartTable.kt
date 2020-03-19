package de.flapdoodle.tab.controls.tables

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
    internal val rows: ObservableList<T>,
    internal val columns: ObservableList<out SmartColumn<T, out Any>>
) : Control() {

  private val skin = SmartTableSkin(this)

  private val rowsChangeListener = ListChangeListener<T> { skin.rowsChanged() }
  private val columnsChangeListener = ListChangeListener<SmartColumn<T, out Any>> { skin.columnsChanged() }

  init {
    rows.addListener(WeakListChangeListener(rowsChangeListener))
    columns.addListener(WeakListChangeListener(columnsChangeListener))

    addClass(SmartTableStyles.smartTable)
  }

  override fun createDefaultSkin() = skin
  fun columns() = skin.headerColumns()

  class SmartTableSkin<T : Any>(
      control: SmartTable<T>
  ) : SkinBase<SmartTable<T>>(control) {


    private val header = SmartHeader(control.columns)
    private val rowsPane = SmartRows(control.rows, header.headerColumns()).apply {
      VBox.setVgrow(this, Priority.ALWAYS)
    }
    private val footer = SmartFooter(control.columns)

    private val scroll = ScrollPane().apply {
      add(rowsPane)
    }

    private val all = VBox().apply {
      add(header)
      add(scroll)
      add(footer)
    }

    init {
      children.add(all)
      columnsChanged()
      rowsChanged()
    }

    internal fun rowsChanged() {
      rowsPane.rowsChanged()
    }

    internal fun columnsChanged() {
      header.columnsChanged()
      rowsPane.columnsChanged()
      footer.columnsChanged()
    }
    internal fun headerColumns() = header.headerColumns()
  }
}