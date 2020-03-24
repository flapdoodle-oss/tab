package de.flapdoodle.tab.controls.tables

import de.flapdoodle.tab.extensions.parentOfType
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.event.EventHandler
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.control.SkinBase
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.util.StringConverter
import tornadofx.*
import java.lang.RuntimeException

open class SmartCell<T : Any, C : Any>(
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
        control.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_RELEASED) {
          if (it.clickCount == 1) {
            control.requestFocus()
          }
          if (it.clickCount == 2) {
            _startEdit()
          }
          it.consume()
        }

        control.addEventHandler(KeyEvent.KEY_RELEASED) {
          if (!it.isShortcutDown) {
            println("#############################")
            println("event $it -> ${it.isConsumed} --> ${it.target} ? $control")
            println("#############################")

            if (it.code == KeyCode.LEFT) {
              it.consume()
              fireEvent(Events.MoveCursor(deltaColumn = -1))
            }
            if (it.code == KeyCode.RIGHT) {
              it.consume()
              fireEvent(Events.MoveCursor(deltaColumn = 1))
            }
            if (it.code == KeyCode.UP) {
              it.consume()
              fireEvent(Events.MoveCursor(deltaRow = -1))
            }
            if (it.code == KeyCode.DOWN) {
              it.consume()
              fireEvent(Events.MoveCursor(deltaRow = 1))
            }
            if (it.code == KeyCode.ENTER) {
              it.consume()
              _startEdit()
            }
          }
        }

        control.focusedProperty().addListener { _, _, focused ->
          if (focused) {
            fireEvent(Events.CellFocused(control))
          }
        }
      }
    }

    private val field = createTextField(
        value = control.value,
        converter = control.converter,
        commitEdit = {
          label.text = control.converter.toString(it)
          control.fireEvent(Events.EditDone(control))
          control.onChange(it)
          _editDone()
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

//    enum class EVT: EventType<EVT> {
//
//    }
//    object EditDone : Event() {
//
//    }

    internal fun _editDone() {
//      control.fireEvent(Events.EditDone(control))
//      control.fireEvent(Events.EditDone(control))

//      val parentTable = control.parentOfType(SmartTable::class)
//      if (parentTable!=null) {
//        println("----------------------------------")
//        println("parent found: -> $parentTable")
//        println("----------------------------------")
//        Event.fireEvent(parentTable, Events.EditDone(control))
//      } else {
//        println("----------------------------------")
//        println("parent NOT found: -> $parentTable")
//        println("----------------------------------")
//      }
      _cancelEdit()
    }

    internal fun _cancelEdit() {
      label.show()
      field.hide()
      field.text = label.text
    }

    internal fun _startEdit() {
      RuntimeException("startEdit called").printStackTrace()
      label.hide()
      field.show()
      field.requestFocus()
    }

    init {
      children.add(field)
      children.add(label)

      consumeMouseEvents(false)

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

//      textField.onAction = EventHandler { event: ActionEvent ->
//        event.consume()
//        commitEdit(converter.fromString(textField.text))
//      }
      textField.onKeyReleased = EventHandler { t: KeyEvent ->
        if (t.code == KeyCode.ENTER) {
          t.consume()
          commitEdit(converter.fromString(textField.text))
        }
        if (t.code == KeyCode.ESCAPE) {
          t.consume()
          cancelEdit()
        }
      }
      return textField
    }
  }
}