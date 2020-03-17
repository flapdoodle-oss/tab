package de.flapdoodle.tab.controls.layout

import javafx.scene.Group
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.Region

class LayoutFun : Region() {
  private val group = Group()

  init {
    group.isManaged = false
    (0..3).forEach {
      when (it % 3) {
        1 -> group.children.add(Label("$it"))
        else -> group.children.add(Button("$it"))
      }
    }
    children.add(group)

    layoutBoundsProperty().addListener { _, _, changed ->
      println("------------------------------------")
      println("layout changed: $changed")
      println("------------------------------------")
    }

    //requestLayout()
    layoutChildren()
  }

  override fun layoutChildren() {
    println("layout...")
    val width = getWidth()

    val minHeight = group.children.map { node -> node.prefHeight(width) }
        .max() ?: 0.0
    val minWidth = group.children.map { node -> node.prefWidth(minHeight) }
        .max() ?: 0.0

    group.children.forEachIndexed { index, node ->
      println("node: $node")
      if (node.isResizable && node.isManaged) {
        println("move ...")
        node.resizeRelocate(0.0,0.0 + index * minHeight,minWidth,minHeight)
      }
    }
  }
}