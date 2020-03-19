package de.flapdoodle.tab.controls.tables

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.control.SkinBase
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.util.StringConverter
import tornadofx.*

open class SmartCell<T: Any, C : Any>(
    val value: C?,
    val editable: Boolean,
    val converter: StringConverter<C>
) : Control() {

  private val skin = SmartCellSkin(this)

  init {
    isFocusTraversable = true

    addClass(SmartTableStyles.smartCell)

//    addClass(TabStyle.spreadSheedCell)
//    if (odd) {
//      addClass(TabStyle.odd)
//    }
  }

  open fun onChange(value: C?) {}

  override fun createDefaultSkin() = skin

  class SmartCellSkin<T : Any, C : Any>(
      private val control: SmartCell<T, C>
  ) : SkinBase<SmartCell<T, C>>(control) {

    private val label = Label().apply {
      isWrapText = false
      prefWidth = Double.MAX_VALUE
      text = control.converter.toString(control.value)
      if (control.editable) {
        addEventHandler(javafx.scene.input.MouseEvent.MOUSE_RELEASED) {
          if (it.clickCount == 2) {
            _edit()
          }
        }
      }
    }

    private val field = createTextField(
        value = control.value,
        converter = control.converter,
        commitEdit = {
          label.text = control.converter.toString(it)
          control.onChange(it)
          _cancelEdit()
        },
        cancelEdit = this::_cancelEdit
    ).apply {
      isVisible = false
      isEditable = true
      focusedProperty().addListener { _, _, focused ->
        if (!focused) {
          _cancelEdit()
        }
      }
    }

    internal fun _cancelEdit() {
      label.show()
      field.hide()
      field.text = label.text
    }

    internal fun _edit() {
      label.hide()
      field.show()
      field.requestFocus()
    }

    init {
      children.add(field)
      children.add(label)

//      control.prefWidthProperty().bind(control.column.widthProperty())
    }

    override fun computeMinWidth(height: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double): Double {
      return java.lang.Double.max(label.minWidth(height), field.minWidth(height))
    }

    override fun computeMinHeight(width: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double): Double {
      return java.lang.Double.max(label.minHeight(width), field.minHeight(width))
    }

//    override fun computePrefWidth(height: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double): Double {
//      var base = if (label.isVisible) label.prefWidth(height) * 2.0 else field.prefWidth(height)
//      base = label.prefWidth(height) * 2.0
//      return base + leftInset + rightInset
//    }

    override fun computePrefHeight(width: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double): Double {
      return java.lang.Double.max(label.prefHeight(width), field.prefHeight(width)) + topInset + bottomInset
    }

  }

  companion object {
    fun <T : Any> createTextField(
        value: T?,
        converter: StringConverter<T>,
        commitEdit: (T?) -> Unit,
        cancelEdit: () -> Unit
    ): TextField {
      val textField = TextField(converter.toString(value))

      textField.onAction = EventHandler { event: ActionEvent ->
        commitEdit(converter.fromString(textField.text))
        event.consume()
      }
      textField.onKeyReleased = EventHandler { t: KeyEvent ->
        if (t.code == KeyCode.ESCAPE) {
          cancelEdit()
          t.consume()
        }
      }
      return textField
    }

  }
}