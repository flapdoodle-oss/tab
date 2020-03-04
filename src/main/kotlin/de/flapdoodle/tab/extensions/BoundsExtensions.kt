package de.flapdoodle.tab.extensions

import de.flapdoodle.tab.bindings.ChangeListeners
import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.binding.Binding
import javafx.beans.binding.ObjectBinding
import javafx.beans.value.ChangeListener
import javafx.beans.value.WeakChangeListener
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import javafx.geometry.Point2D
import javafx.scene.Node
import org.fxmisc.easybind.EasyBind
import java.lang.RuntimeException
import java.util.concurrent.atomic.AtomicBoolean

fun Bounds.min(): Point2D {
  return Point2D(this.minX, this.minY)
}

fun Bounds.centerOf(): Point2D {
  val x = (minX + maxX) / 2
  val y = (minY + maxY) / 2
  return Point2D(x, y)
}

fun Node.mappedBounds(child: Node, localBounds: Bounds): Bounds {
  val parent = child.parent
  var bounds = child.localToParent(localBounds)
  if (this !== parent) {
    bounds = this.mappedBounds(parent, bounds)
  }
  return bounds
}

fun Node.boundsInTop(childInTop: Node): Bounds {
  return this.mappedBounds(childInTop, childInTop.boundsInLocal)
}

fun Node.map(childInTop: Node, coord: Point2D): Point2D {
  return this.mappedBounds(childInTop, BoundingBox(coord.x, coord.y, 0.0, 0.0)).min()
}

fun Node.centerInTop(child: Node): Binding<Point2D> {
  return CenterBinding(this,child)
}

class CenterBinding(
    private val top: Node,
    private val child: Node
) : ObjectBinding<Point2D>() {

  // see https://stackoverflow.com/questions/45117076/javafx-invalidationlistener-or-changelistener
  private val observer = ChangeListener<Any> { _, _, _ ->
    this.invalidate()
  }

  init {
    top.parentPath(child).forEach {
      it.boundsInLocalProperty().addListener(WeakChangeListener(observer))
    }
  }

  override fun computeValue(): Point2D {
    try {
      return top.mappedBounds(child, child.boundsInLocal).centerOf()
    } catch (ex: Exception) {
      return Point2D(0.0,0.0)
    }
  }
}
