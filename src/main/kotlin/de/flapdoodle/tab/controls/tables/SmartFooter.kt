package de.flapdoodle.tab.controls.tables

import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.scene.Group
import javafx.scene.control.Control
import javafx.scene.control.Skin
import javafx.scene.control.SkinBase
import javafx.scene.control.SplitPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import tornadofx.*

class SmartFooter<T : Any>(
    private val columns: ObservableList<out SmartColumn<T, out Any>>
) : Control() {

  private val skin = SmartFooterSkin(this)

  init {
    addClass(SmartTableStyles.smartFooter)
  }

  internal fun columnsChanged() {
    skin.columnsChanged()
  }

  override fun createDefaultSkin() = skin

  class SmartFooterSkin<T : Any>(
      private val src: SmartFooter<T>
  ) : SkinBase<SmartFooter<T>>(src) {
    private val footer = HBox().apply {

//      prefWidth = 200.0
    }


    internal fun columnsChanged() {
      footer.children.setAll(src.columns.map { FooterColumn(it).apply {
        prefWidthProperty().bind(it.widthProperty())
      } })
    }

    init {
      children.add(footer)
//      columnsChanged()
    }
  }

  class FooterColumn<T: Any>(
      private val column: SmartColumn<T, out Any>
  ) : Control() {

    private val skin = Skin(this)
    override fun createDefaultSkin() = skin

    class Skin<T: Any>(control: FooterColumn<T>) : SkinBase<FooterColumn<T>>(control) {
      val stackPane = StackPane()

      init {
        if (control.column.footer!=null) {
          stackPane.add(control.column.footer)
        }
//      stackPane.prefWidthProperty().bind(column.widthProperty())
        children.add(stackPane)
      }

    }
  }
}