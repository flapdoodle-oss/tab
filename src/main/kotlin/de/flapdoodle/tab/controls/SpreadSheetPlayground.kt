package de.flapdoodle.tab.controls

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Group
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Control
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.paint.Color
import javafx.util.StringConverter
import javafx.util.converter.DefaultStringConverter
//import org.controlsfx.control.spreadsheet.GridBase
//import org.controlsfx.control.spreadsheet.SpreadsheetCell
//import org.controlsfx.control.spreadsheet.SpreadsheetCellType
//import org.controlsfx.control.spreadsheet.SpreadsheetView
import tornadofx.*

class SpreadSheetPlayground : Fragment() {

  //  override val root = group {
//      val rowCount = 15
//      val columnCount = 10
//      val grid = GridBase(rowCount, columnCount)
//
//      val rows: ObservableList<ObservableList<SpreadsheetCell>> = FXCollections.observableArrayList<ObservableList<SpreadsheetCell>>()
//      for (row in 0 until grid.getRowCount()) {
//        val list: ObservableList<SpreadsheetCell> = FXCollections.observableArrayList<SpreadsheetCell>()
//        for (column in 0 until grid.getColumnCount()) {
//          list.add(SpreadsheetCellType.STRING.createCell(row, column, 1, 1, "value"))
//        }
//        rows.add(list)
//      }
//      grid.setRows(rows)
//
//      val spv = SpreadsheetView(grid)
//    }
  override val root = gridpane {
    style {
      backgroundColor = multi(Color.WHITE)
      borderColor += box(Color.RED)
      borderWidth += box(1.0.px)
    }
    row {
      button("A")
      button("B")
      button("C")
    }
    row {
      pane {
        style {
          backgroundColor = multi(Color(0.0,0.0,0.0, 0.2))
          borderWidth += box(1.0.px)
          borderColor += box(Color.BLACK)
          padding = box(2.0.px)
        }
        button("pane")
      }
    }

    this.add(Button("X---------"), 2, 3)
    add(TextFieldTableCell<String, String>(DefaultStringConverter()).apply {
      text = "Wooohooo"
    }, 2, 2)
  }

}