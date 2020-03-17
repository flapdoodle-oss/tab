package de.flapdoodle.tab.controls

import de.flapdoodle.tab.styles.TabStyle
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.control.Skin
import javafx.scene.control.SkinBase
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseEvent
import tornadofx.*
import java.lang.Double.max

class SpreadSheetCell(content: String, odd: Boolean) : Control() {

  private val textProperty = SimpleStringProperty(this, "text")

  init {
    isFocusTraversable = true
    textProperty.value = content

    addClass(TabStyle.spreadSheedCell)
    if (odd) {
      addClass(TabStyle.odd)
    }
  }

  override fun createDefaultSkin(): Skin<*> {
    return SpreadSheetCellSkin(this)
  }

  class SpreadSheetCellSkin(cell: SpreadSheetCell) : SkinBase<SpreadSheetCell>(cell) {
    private val label = Label()
    private val field = TextField()

    init {
      label.textProperty().bind(cell.textProperty)
      label.isWrapText = false
      label.addEventHandler(MouseEvent.MOUSE_RELEASED) {
        if (it.clickCount == 2) {
          println("-----------------------")
          println("edit???")
          println("-----------------------")
          label.isVisible = false
          field.isVisible = true
          field.text = label.text
          field.requestFocus()
        }
      }

      field.isVisible = false
      field.isEditable = true
      field.focusedProperty().addListener { observable, oldValue, focused ->
        if (!focused) {
          label.isVisible = true
          field.isVisible = false
        }
      }
      field.setOnKeyReleased {

        val hide = when (it.code) {
          KeyCode.ENTER -> {
            cell.textProperty.set(field.text)
            true
          }
          KeyCode.ESCAPE -> true
          else -> false
        }
        if (hide) {
          label.isVisible = true
          field.isVisible = false
        }
      }


      cell.getChildList()!!.add(label)
      cell.getChildList()!!.add(field)
    }

    override fun computeMinWidth(height: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double): Double {
      return max(label.minWidth(height), field.minWidth(height))
    }

    override fun computeMinHeight(width: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double): Double {
      return max(label.minHeight(width), field.minHeight(width))
    }

    override fun computePrefWidth(height: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double): Double {
      var base = if (label.isVisible) label.prefWidth(height) * 2.0 else field.prefWidth(height)
      base = label.prefWidth(height)*2.0
      return base + leftInset + rightInset
    }

    override fun computePrefHeight(width: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double): Double {
      return max(label.prefHeight(width),field.prefHeight(width)) + topInset + bottomInset
    }
  }
}