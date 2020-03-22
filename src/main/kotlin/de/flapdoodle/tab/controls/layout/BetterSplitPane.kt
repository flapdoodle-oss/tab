package de.flapdoodle.tab.controls.layout

import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.control.Skin
import javafx.scene.control.SkinBase
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import tornadofx.*

class BetterSplitPane(

) : Control() {

  private val nodes = FXCollections.observableArrayList<Node>()
  private val skin = Skin(this)

  init {
    style {
      borderColor += box(Color.GREEN)
      borderWidth += box(1.px)
    }
  }

  override fun createDefaultSkin() = skin
  fun add(node: Node) {
    nodes.add(node)
  }

  class Skin(
      private val control: BetterSplitPane
  ) : SkinBase<BetterSplitPane>(control) {

    private val handles = FXCollections.observableArrayList<SplitHandle>()

    init {
      control.nodes.addListener(ListChangeListener {
        handles.setAll(control.nodes.map {
          SplitHandle(it)
        })

        children.setAll(
            control.nodes + handles
        )
        control.requestLayout()
      })
    }

    override fun computePrefWidth(height: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double): Double {
//      super.computePrefWidth(height,topInset,rightInset,bottomInset,leftInset)
      val ret = children.filter { it.isManaged }
          .map { it.prefWidth(height) }
          .sumByDouble { it -> snapSizeX(it) }

      println("pref width -> $ret")
      return ret
    }

    override fun layoutChildren(contentX: Double, contentY: Double, contentWidth: Double, contentHeight: Double) {
      val managedChildren = control.nodes.filter { it.isManaged }
      val nodeWidthMap = managedChildren.map { it to it.prefWidth(contentHeight) }

      var currentX = contentX
      nodeWidthMap.forEach { (node,w) ->
        layoutInArea(node, currentX, contentY, w, contentHeight, -1.0, HPos.CENTER, VPos.CENTER)
        currentX = currentX + w
      }

      handles.forEach {
        val pos = it.node.layoutBounds.width + it.node.layoutX - it.minWidth/2.0
        layoutInArea(it, pos, contentY, it.minWidth, contentHeight, -1.0, HPos.CENTER, VPos.CENTER)
      }
    }
  }

  class SplitHandle(
      internal val node: Node
  ) : Control() {
    private val skin = Skin(this)

    override fun createDefaultSkin() = skin

    class Skin(control: SplitHandle) : SkinBase<SplitHandle>(control) {
      init {
        children.add(Label("|"))

        control.style {
          borderWidth += box(1.px)
          borderColor += box(Color.RED)
          padding = box(0.px, 5.px)
        }

        control.addEventHandler(MouseEvent.MOUSE_ENTERED) {event ->
          control.style {
            borderColor += box(Color.GREEN)
          }
        }
        control.addEventHandler(MouseEvent.MOUSE_EXITED) { event ->
          control.style {
            borderColor += box(Color.BLUE)
          }
        }
      }
    }
  }
}