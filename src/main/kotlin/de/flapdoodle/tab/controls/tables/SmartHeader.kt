package de.flapdoodle.tab.controls.tables

import de.flapdoodle.tab.styles.TabStyle
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.control.SkinBase
import javafx.scene.control.SplitPane
import tornadofx.*

class SmartHeader<T : Any>(
    private val columns: ObservableList<Column<T, out Any>>
) : Control() {

  private val skin = SmartHeaderSkin(this)

  init {
    addClass(SmartTableStyles.smartHeader)
  }

  internal fun columnsChanged() {
    skin.columnsChanged()
  }

  override fun createDefaultSkin() = skin
  fun headerColumns() = skin.headerColumns()

  class SmartHeaderSkin<T : Any>(
      private val src: SmartHeader<T>
  ) : SkinBase<SmartHeader<T>>(src) {
    private val header = SplitPane().apply {
//      addClass(TabStyle.styledSplitPane)
      prefWidth = 200.0
    }

//    private val headerColumns = FXCollections.observableArrayList<ColumnHeader<T>>()

    internal fun columnsChanged() {
      header.items.bind(src.columns) { SmartColumnHeader(it) }
      header.items.addListener(ListChangeListener {
        val size = it.list.size
        if (size > 0) {
          val offset = 1.0 / size
          (0 until size).forEach { index ->
            header.setDividerPosition(index, offset * (index + 1))
          }
        }
      })

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
    }

    @Suppress("UNCHECKED_CAST")
    fun headerColumns() = header.items.asUnmodifiable() as ObservableList<out SmartColumn<T, out Any>>
  }

  class SmartColumnHeader<T : Any, C: Any>(
      override val column: Column<T, C>
  ) : Label(column.columnName), SmartColumn<T, C> {
    init {
      maxWidth = 200.0

      widthProperty()
    }
  }
}