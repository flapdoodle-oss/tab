package de.flapdoodle.tab.controls.tables

import de.flapdoodle.tab.extensions.property
import javafx.collections.ObservableList
import javafx.scene.control.Control
import javafx.scene.control.Skin
import javafx.scene.control.SkinBase
import javafx.scene.layout.HBox
import tornadofx.*

class SmartRow<T : Any>(
    internal val columns: ObservableList<out SmartColumn<T, out Any>>,
    internal val value: T,
    internal val index: Int
) : Control() {

  private val skin = SmartRowSkin(this)

  init {
    isFocusTraversable = false
    addClass(SmartTableStyles.smartRow)
    if (index % 2 == 0) {
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

  internal fun setCursor(cursor: Cursor<T>) {
    skin.setCursor(cursor)
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

      row.addEventFilter(SmartEvents.ALL) { event ->
        when (event) {
          is SmartEvents.EditDone -> {
            event.consume()
            println("Row: EditDone in ${event.cell}")
            row.fireEvent(SmartEvents.MoveCursor(deltaRow = 1))
          }
          is SmartEvents.CellFocused -> {
            event.consume()
            println("Cell focused: ${event.cell}")

            val column = event.cell.property(SmartColumn::class)
            val matchingColumn = row.columns.find { it == column }
            require(matchingColumn!=null) {"column not found: $column -> ${row.columns}"}
            row.fireEvent(SmartEvents.ChangeCursor(Cursor(matchingColumn, row.index)))
          }
          is SmartEvents.SetCursor<out Any> -> {
            setCursor(event.cursor as Cursor<T>)
          }
          else -> println("$event")
        }
//        event.consume()
      }
    }

    fun setCursor(cursor: Cursor<T>) {
      if (cursor.row==row.index) {
        println("set cursor ${cursor} matches")
        val cell = rowContainer.children.find {
          val cellColumn = it.property(SmartColumn::class)
          println("$cellColumn ? ${cursor.column} -> ${it.properties}")
          cellColumn == cursor.column
        }
        println("request focus for ${cursor} -> $cell (${cell?.isFocused})")
        if (cell!=null && !cell.isFocused) {
          println("do it for ${cell}")
          cell.requestFocus()
        }
      }
    }

    private fun <T : Any, C : Any> cell(c: SmartColumn<T, C>, row: T): SmartCell<T, C> {
      return c.cell(row).apply {
        property(SmartColumn::class, c)
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