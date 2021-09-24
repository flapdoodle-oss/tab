package de.flapdoodle.fx.graph

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Orientation
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.ScrollBar
import javafx.scene.layout.Region
import javafx.scene.shape.Rectangle
import javafx.scene.transform.Scale

open class PanningWindow : Region() {

    companion object {
        private const val SCALE_MIN = 0.5f
        private const val SCALE_MAX = 1.5f
    }

    private var content: Region? = null

    private val contentX: DoubleProperty = object : SimpleDoubleProperty() {
        override fun invalidated() {
            requestLayout()
        }
    }
    private val contentY: DoubleProperty = object : SimpleDoubleProperty() {
        override fun invalidated() {
            requestLayout()
        }
    }
    private val scrollX = ScrollBar()
    private val scrollY = ScrollBar()

    private val clickPosition: Point2D? = null
    private val windowPosAtClick: Point2D? = null

    private val zoom: DoubleProperty = SimpleDoubleProperty(1.0)
    private val scale = Scale()

    init {
        val clip = Rectangle()
        clip.widthProperty().bind(widthProperty())
        clip.heightProperty().bind(heightProperty())
        //setClip(clip)

        scale.xProperty().bind(zoom)
        scale.yProperty().bind(zoom)

        children.addAll(scrollX, scrollY)

        scrollX.orientation = Orientation.HORIZONTAL
        scrollX.valueProperty().bindBidirectional(contentX)
        scrollX.styleClass.add("graph-editor-scroll-bar") //$NON-NLS-1$


        scrollY.valueProperty().bindBidirectional(contentY)
        scrollY.orientation = Orientation.VERTICAL
        scrollY.styleClass.add("graph-editor-scroll-bar") //$NON-NLS-1$
    }

    override fun layoutChildren() {
        super.layoutChildren()
        val height = height
        val width = width
        val theContent: Node? = content

        // content
        theContent?.relocate(-contentX.get(), -contentY.get())

        // scrollbars
        val w = scrollY.width
        val h = scrollX.height
        scrollX.resizeRelocate(0.0, snapPositionY(height - h), snapSizeX(width - w), h)
        scrollY.resizeRelocate(snapPositionX(width - w), 0.0, w, snapSizeY(height - h))
        val zoomFactor: Double = theContent?.localToSceneTransform?.mxx ?: 1.0
        scrollX.min = 0.0
        scrollX.max = getMaxX()
        scrollX.visibleAmount = zoomFactor * width
        scrollY.min = 0.0
        scrollY.max = getMaxY()
        scrollY.visibleAmount = zoomFactor * height
    }

    private fun getMaxX(): Double {
        val theContent = content
        if (theContent != null) {
            val zoomFactor = theContent.localToSceneTransform.mxx
            return zoomFactor * theContent.width - width
        }
        return 0.0
    }

    private fun getMaxY(): Double {
        val theContent = content
        if (theContent != null) {
            val zoomFactor = theContent.localToSceneTransform.mxx
            return zoomFactor * theContent.height - height
        }
        return 0.0
    }


    protected fun setContent(pContent: Region?) {
        // Remove children and release bindings from old content, if any exists.

        // Remove children and release bindings from old content, if any exists.
        val prevContent = content
        if (prevContent != null) {
            //removeMouseHandlersFromContent(prevContent)
            children.remove(prevContent)
            prevContent.transforms.remove(scale)
        }

        this.content = pContent

        if (pContent != null) {
            pContent.setManaged(false)
            children.add(pContent)
            //addMouseHandlersToContent(pContent)
            pContent.getTransforms().add(scale)
            scrollX.isVisible = true
            scrollY.isVisible = true
        } else {
            scrollX.isVisible = false
            scrollY.isVisible = false
        }
    }

    fun scrollTo(x: Double, y: Double) {
        panTo(x,y);

        contentX.set(x)
        contentY.set(y)
    }

    open fun panTo(x: Double, y: Double) {
        println("panTo: $x,$y")
        if (canNotPan()) {
            return
        }
        val newX: Double = checkContentX(x)
        val newY: Double = checkContentY(y)
        println("panTo -> newXY: $newX,$newY")
        if (newX != contentX.get() || newY != contentY.get()) {
            contentX.set(newX)
            contentY.set(newY)
        }
    }

    private fun checkContentX(xToCheck: Double): Double {
        return snapPositionX(Math.min(getMaxX(), Math.max(xToCheck, 0.0)))
    }

    private fun checkContentY(yToCheck: Double): Double {
        return snapPositionY(Math.min(getMaxY(), Math.max(yToCheck, 0.0)))
    }


    private fun canNotPan(): Boolean {
        println("canNotPan -> ${content!!.width} ? $width")
        return content == null || content!!.width < width
    }

}