package de.flapdoodle.tab.controls.layout

import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.geometry.HPos
import javafx.geometry.Point2D
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.control.Skin
import javafx.scene.control.SkinBase
import javafx.scene.input.MouseDragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import tornadofx.*
import java.lang.Double.max

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
          SplitHandle(this, it)
        })

        children.setAll(
            control.nodes + handles
        )
        control.requestLayout()
      })

      if (true) {
        var dragStarted: DragStart? = null
        control.addEventFilter(MouseEvent.MOUSE_PRESSED) { event ->
//          println("mouse clicked in parent")
          val target = event.target
          if (target is SplitHandle && target.parentSkin == this) {
//            println("start")
            dragStarted = DragStart(Point2D(event.x, event.y), target)

            event.isDragDetect = true
            event.consume()
          } else {
//            println("skip ${event.target}")
          }
        }

        control.addEventFilter(MouseEvent.DRAG_DETECTED) { event ->
//        dragStarted = dragStarted?.copy(pos = Point2D(event.x, event.y))
          control.startFullDrag()
          event.consume()
        }

        control.addEventFilter(MouseDragEvent.MOUSE_DRAGGED) { event ->
          if (dragStarted != null) {
            val start = dragStarted
            require(start != null) { "drag not started.." }
            val current = Point2D(event.x, event.y)

            val localStart = control.sceneToLocal(start.pos)
            val localCurrent = control.sceneToLocal(current)

            val diff = current - start.pos

//            println("${start.handle.node}: from $dragStarted to $current -> $diff (local)")


//            if (diff.x > 20.0 || diff.x < -20.0) {
//              println("###################################")
//              println("# WHAT                            #")
//              println("###################################")
//            }
            start.handle.changedPrefWidth = start.currentWith + diff.x
            start.handle.requestLayout()
            //        //node.prefWidth = node.layoutBounds.width + diff.x
            event.consume()
          }
        }
        control.addEventFilter(MouseEvent.MOUSE_RELEASED) { event ->
          if (dragStarted != null) {
            dragStarted = null
            event.consume()
          }
        }
      }
    }

    override fun computePrefWidth(height: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double): Double {
//      super.computePrefWidth(height,topInset,rightInset,bottomInset,leftInset)
      val ret = handles.filter { it.isNodeManaged() }
          .map { it.prefWidthOfNode(height) }
          .sumByDouble { it -> snapSizeX(it) }

//      println("pref width -> $ret")
      return ret
    }

    override fun layoutChildren(contentX: Double, contentY: Double, contentWidth: Double, contentHeight: Double) {
      val managedChildren = handles.filter { it.isNodeManaged() }
      val nodeWidthMap = managedChildren.map {
        it to it.prefWidthOfNode(contentHeight)
      }

//      println("-------------------------")
      var currentX = contentX
//      println("start $currentX")
      nodeWidthMap.forEach { (handle, w) ->
//        println("layout ${handle.node} --> $w")
        layoutInArea(handle.node, currentX, contentY, w, contentHeight, -1.0, HPos.CENTER, VPos.CENTER)
        currentX = currentX + w
//        println("... $currentX")
      }
//      println("-------------------------")

      currentX = contentX
      nodeWidthMap.forEach { (it, w) ->
        val pos = currentX + w - it.prefWidth / 2.0
//        println("${it.node} - w=${it.node.layoutBounds.width}, x=${it.node.layoutX} - ${it.changedPrefWidth}")
        layoutInArea(it, pos, contentY, it.prefWidth, contentHeight, -1.0, HPos.CENTER, VPos.CENTER)
        currentX = currentX + w
      }
    }
  }

  data class DragStart(
      val pos: Point2D,
      val handle: SplitHandle,
      val currentWith: Double = handle.changedPrefWidth ?: 0.0
  )

  class SplitHandle(
      internal val parentSkin: BetterSplitPane.Skin,
      internal val node: Node
  ) : Control() {
    private val skin = Skin(this)
    var changedPrefWidth: Double? = null

    override fun createDefaultSkin() = skin

    fun prefWidthOfNode(height: Double): Double {
      val ret = node.prefWidth(height) + (changedPrefWidth ?: 0.0)
      val min = node.minWidth(height)
      //println("$node prefWidth -> $ret")
      return max(min, ret)
    }

    fun isNodeManaged(): Boolean {
      return node.isManaged
    }


    class Skin(control: SplitHandle) : SkinBase<SplitHandle>(control) {
      init {
        children.add(Label("|"))

        control.style {
          borderWidth += box(1.px)
          borderColor += box(Color.RED)
          padding = box(0.px, 5.px)
        }

        if (false) {
          var dragStarted: DragStart? = null

          control.addEventFilter(MouseEvent.ANY) { event ->
            println("--> $event")
          }

          control.addEventHandler(MouseEvent.MOUSE_PRESSED) { event ->
            event.isDragDetect = true
            dragStarted = DragStart(Point2D(event.x, event.y), control)
            println("node.width: ${node.layoutBounds.width}")
            control.changedPrefWidth = 0.0

            control.style {
              borderColor += box(Color.GREEN)
              padding = box(0.px, 5.px)
            }

            event.consume()
          }
          control.addEventHandler(MouseDragEvent.DRAG_DETECTED) { event ->
            println("drag detected")
            event.consume()
            control.parent.startFullDrag()
          }
          control.addEventHandler(MouseDragEvent.MOUSE_DRAGGED) { event ->
            val start = dragStarted
            require(start != null) { "drag not started.." }
            val current = Point2D(event.x, event.y)

            val localStart = control.sceneToLocal(start.pos)
            val localCurrent = control.sceneToLocal(current)

            val diff = current - start.pos

            println("${control.node}: from $dragStarted to $current -> $diff (local)")

            if (diff.x > 20.0 || diff.x < -20.0) {
              println("###################################")
              println("# WHAT                            #")
              println("###################################")
            }
            control.changedPrefWidth = start.currentWith + diff.x
            control.requestLayout()
            //node.prefWidth = node.layoutBounds.width + diff.x
            event.consume()
          }
          control.addEventHandler(MouseEvent.MOUSE_RELEASED) { event ->
            dragStarted = null
            control.style {
              borderColor += box(Color.BLUE)
              padding = box(0.px, 5.px)
            }
            event.consume()
          }
        }
      }
    }
  }
}