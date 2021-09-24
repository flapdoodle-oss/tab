package de.flapdoodle.fx.layout.panning

import de.flapdoodle.fx.layout.helper.Cross
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.control.ScrollBar
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class VirtualPane : Region() {
    private val contentContainer = Container()
    private val scrollX = ScrollBar()
    private val scrollY = ScrollBar()

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
    
    init {
        background = Background(
            BackgroundFill(
                Color.rgb(255, 255, 255, 0.4),
                CornerRadii.EMPTY,
                Insets.EMPTY
            )
        )

        scrollX.orientation = Orientation.HORIZONTAL
        scrollX.valueProperty().bindBidirectional(contentX)
        scrollX.styleClass.add("graph-editor-scroll-bar") //$NON-NLS-1$


        scrollY.valueProperty().bindBidirectional(contentY)
        scrollY.orientation = Orientation.VERTICAL
        scrollY.styleClass.add("graph-editor-scroll-bar") //$NON-NLS-1$


        children.addAll(contentContainer, Cross(), Cross(100.0, 100.0), scrollX, scrollY)





        setPrefSize(100.0, 100.0)

        val clipRect = Rectangle(getWidth(), getHeight())
        clipRect.heightProperty().bind(heightProperty())
        clipRect.widthProperty().bind(widthProperty())
        setClip(clipRect)


    }

    override fun layoutChildren() {
        super.layoutChildren()

        contentContainer.relocate(-contentX.get(), -contentY.get())

        val w = scrollY.width
        val h = scrollX.height
        scrollX.resizeRelocate(0.0, snapPositionY(height - h), snapSizeX(width - w), h)
        scrollY.resizeRelocate(snapPositionX(width - w), 0.0, w, snapSizeY(height - h))
        val zoomFactor: Double = contentContainer.localToSceneTransform.mxx

        scrollX.min = 0.0
        scrollX.max = getMaxX()
        scrollX.visibleAmount = zoomFactor * width
        scrollY.min = 0.0
        scrollY.max = getMaxY()
        scrollY.visibleAmount = zoomFactor * height

        scrollX.isVisible = scrollX.max>scrollX.visibleAmount
        scrollY.isVisible = scrollY.max>scrollY.visibleAmount
        
//        println("x-> ${scrollX.max} ?? ${scrollX.visibleAmount}")
//        println("y-> ${scrollY.max} ?? ${scrollY.visibleAmount}")
    }

    private fun getMaxX(): Double {
        val zoomFactor = contentContainer.localToSceneTransform.mxx
        return zoomFactor * contentContainer.width - width
    }

    private fun getMaxY(): Double {
        val zoomFactor = contentContainer.localToSceneTransform.mxx
        return zoomFactor * contentContainer.height - height
    }


    fun setContent(content: Region?) {
        contentContainer.setContent(content)
    }

//    private class ContentContainer : Region() {
//        init {
//            setPrefSize(100.0, 100.0)
//            background = Background(
//                BackgroundFill(
//                    Color.rgb(255, 0, 0, 0.1),
//                    CornerRadii.EMPTY,
//                    Insets.EMPTY
//                )
//            )
//        }
//
//        fun setContent(content: Region?) {
//            children.clear()
//            if (content!=null) {
//                children.add(content)
//            }
//        }
//    }
}