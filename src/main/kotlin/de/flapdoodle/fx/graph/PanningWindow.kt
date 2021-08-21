package de.flapdoodle.fx.graph

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.collections.ObservableList
import javafx.event.Event
import javafx.event.EventHandler
import javafx.geometry.*
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.ScrollBar
import javafx.scene.input.*
import javafx.scene.layout.Region
import javafx.scene.shape.Rectangle
import javafx.scene.transform.Scale


open class PanningWindow : Region() {
    companion object {
        private val SCALE_MIN = 0.5f
        private val SCALE_MAX = 1.5f

        private fun constrainZoom(pZoom: Double): Double {
            val zoom = Math.round(pZoom * 100.0) / 100.0
            return if (zoom <= 1.02 && zoom >= 0.98) {
                1.0
            } else Math.min(Math.max(zoom, SCALE_MIN.toDouble()), SCALE_MAX.toDouble())
        }
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

    private val mousePressedHandler =
        EventHandler { event: MouseEvent? ->
            handlePanningMousePressed(
                event!!
            )
        }
    private val mouseDraggedHandler =
        EventHandler { event: MouseEvent? ->
            handlePanningMouseDragged(
                event!!
            )
        }
    private val mouseReleasedHandler =
        EventHandler { pEvent: MouseEvent? ->
            handlePanningMouseReleased(
                pEvent!!
            )
        }

    private val touchPressedHandler =
        EventHandler { event: TouchEvent? ->
            handlePanningTouchPressed(
                event!!
            )
        }
    private val touchDraggedHandler =
        EventHandler { event: TouchEvent? ->
            handlePanningTouchDragged(
                event!!
            )
        }
    private val touchReleasedHandler =
        EventHandler { event: TouchEvent? ->
            handlePanningFinished(
                event!!
            )
        }

    private val zoomHandler =
        EventHandler { pEvent: ZoomEvent? -> handleZoom(pEvent!!) }
    private val scrollHandler =
        EventHandler { pEvent: ScrollEvent? -> handleScroll(pEvent!!) }
    
    private var clickPosition: Point2D? = null
    private var windowPosAtClick: Point2D? = null

    private val zoom: DoubleProperty = SimpleDoubleProperty(1.0)
    private val scale = Scale()

    init {
        val clip = Rectangle()
        clip.widthProperty().bind(widthProperty())
        clip.heightProperty().bind(heightProperty())
        setClip(clip)

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

    fun panTo(x: Double, y: Double) {
        if (canNotPan()) {
            return
        }
        val newX: Double = checkContentX(x)
        val newY: Double = checkContentY(y)
        if (newX != getContentX() || newY != getContentY()) {
            contentX.set(newX)
            contentY.set(newY)
        }
    }

    private fun canNotPan(): Boolean {
        return content?.let { it.width<width } ?:true
    }

    fun panToX(x: Double) {
        if (canNotPan()) {
            return
        }
        val newX: Double = checkContentX(x)
        if (newX != getContentX()) {
            contentX.set(newX)
        }
    }

    fun panToY(y: Double) {
        if (canNotPan()) {
            return
        }
        val newY: Double = checkContentY(y)
        if (newY != contentY.get()) {
            contentY.set(newY)
        }
    }

    fun panTo(position: Pos) {
        var x = 0.0
        var y = 0.0
        when (position.hpos) {
            HPos.LEFT -> x = 0.0
            HPos.CENTER -> x = (content!!.width - width) / 2
            HPos.RIGHT -> x = content!!.width - width
            else -> {
            }
        }
        when (position.vpos) {
            VPos.TOP -> y = 0.0
            VPos.CENTER -> y = (content!!.height - height) / 2
            VPos.BOTTOM -> y = content!!.height - height
            else -> {
            }
        }
        contentX.set(x)
        contentY.set(y)
        checkWindowBounds()
    }

    fun getContentX(): Double {
        return contentX.get()
    }
    fun getContentY(): Double {
        return contentY.get()
    }
    fun zoomProperty(): DoubleProperty? {
        return zoom
    }

    fun setZoom(pZoom: Double) {
        setZoomAt(pZoom, getContentX(), getContentY())
    }

    fun setZoomAt(pZoom: Double, pPivotX: Double, pPivotY: Double) {
        val oldZoomLevel: Double = getZoom()
        val newZoomLevel: Double = PanningWindow.constrainZoom(pZoom)
        if (newZoomLevel != oldZoomLevel) {
            val f = newZoomLevel / oldZoomLevel - 1
            zoom.set(newZoomLevel)
            panTo(getContentX() + f * pPivotX, getContentY() + f * pPivotY)
        }
    }

    fun getZoom(): Double {
        return zoom.get()
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

    protected fun checkWindowBounds() {
        panTo(getContentX(), getContentY())
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

    private fun checkContentX(xToCheck: Double): Double {
        return snapPositionX(Math.min(getMaxX(), Math.max(xToCheck, 0.0)))
    }

    private fun checkContentY(yToCheck: Double): Double {
        return snapPositionY(Math.min(getMaxY(), Math.max(yToCheck, 0.0)))
    }

    override fun getChildren(): ObservableList<Node> {
        return super.getChildren()
    }

    protected fun setContent(pContent: Region) {
        // Remove children and release bindings from old content, if any exists.
        val prevContent = content
        if (prevContent != null) {
            removeMouseHandlersFromContent(prevContent)
            children!!.remove(prevContent)
            prevContent.transforms.remove(scale)
        }
        content = pContent
        if (pContent != null) {
            pContent.isManaged = false
            children!!.add(pContent)
            addMouseHandlersToContent(pContent)
            pContent.transforms.add(scale)
            scrollX.isVisible = true
            scrollY.isVisible = true
        } else {
            scrollX.isVisible = false
            scrollY.isVisible = false
        }
    }

    protected open fun handlePanningMousePressed(event: MouseEvent) {
        if (activateGesture(GraphInputGesture.PAN, event, this)) {
            startPanning(event.screenX, event.screenY)
        }
    }

    protected open fun handlePanningMouseReleased(pEvent: MouseEvent) {
        handlePanningFinished(pEvent)
    }

    protected open fun handlePanningMouseDragged(event: MouseEvent) {
        if (activateGesture(GraphInputGesture.PAN, event, this)) {
            if (Cursor.MOVE != cursor) {
                startPanning(event.screenX, event.screenY)
            }
            val deltaX = event.screenX - clickPosition!!.x
            val deltaY = event.screenY - clickPosition!!.y
            val newWindowX = windowPosAtClick!!.x - deltaX
            val newWindowY = windowPosAtClick!!.y - deltaY
            panTo(newWindowX, newWindowY)
        }
    }

    protected open  fun handlePanningFinished(event: Event) {
        if (finishGesture(GraphInputGesture.PAN, this)) {
            cursor = null
            event.consume()
        }
    }

    protected open  fun handlePanningTouchPressed(event: TouchEvent) {
        if (activateGesture(GraphInputGesture.PAN, event, this)) {
            startPanning(event.touchPoint.screenX, event.touchPoint.screenY)
        }
    }

    protected open  fun handlePanningTouchDragged(event: TouchEvent) {
        if (activateGesture(GraphInputGesture.PAN, event, this)) {
            if (Cursor.MOVE != cursor) {
                startPanning(event.touchPoint.screenX, event.touchPoint.screenY)
            }
            val deltaX = event.touchPoint.screenX - clickPosition!!.x
            val deltaY = event.touchPoint.screenY - clickPosition!!.y
            val newWindowX = windowPosAtClick!!.x - deltaX
            val newWindowY = windowPosAtClick!!.y - deltaY
            panTo(newWindowX, newWindowY)
        }
    }

    private fun handleScroll(pEvent: ScrollEvent) {
        // this intended for mouse-scroll events (event direct == false)
        // the event also gets synthesized from touch events, which we want to ignore as they are handled in handleZoom()
        if (pEvent.isDirect || pEvent.touchCount > 0 || properties == null) {
            return
        }
        if (activateGesture(GraphInputGesture.ZOOM, pEvent, this)) {
            try {
                val modifier = if (pEvent.deltaY > 1) 0.06 else -0.06
                setZoomAt(getZoom() + modifier, pEvent.x, pEvent.y)
                pEvent.consume()
            } finally {
                finishGesture(GraphInputGesture.ZOOM, this)
            }
        } else if (activateGesture(GraphInputGesture.PAN, pEvent, this)) {
            try {
                panTo(getContentX() - pEvent.deltaX, getContentY() - pEvent.deltaY)
                pEvent.consume()
            } finally {
                finishGesture(GraphInputGesture.PAN, this)
            }
        }
    }

    private fun handleZoom(pEvent: ZoomEvent) {
        if (pEvent.eventType == ZoomEvent.ZOOM_STARTED && activateGesture(
                GraphInputGesture.ZOOM,
                pEvent,
                this
            )
        ) {
            pEvent.consume()
        } else if (pEvent.eventType == ZoomEvent.ZOOM_FINISHED && finishGesture(
                GraphInputGesture.ZOOM,
                this
            )
        ) {
            pEvent.consume()
        } else if (pEvent.eventType == ZoomEvent.ZOOM && activateGesture(
                GraphInputGesture.ZOOM,
                pEvent,
                this
            )
        ) {
            val newZoomLevel = getZoom() * pEvent.zoomFactor
            setZoomAt(newZoomLevel, pEvent.x, pEvent.y)
            pEvent.consume()
        }
    }

    private fun addMouseHandlersToContent(pContent: Node) {
        pContent.addEventHandler(MouseEvent.MOUSE_PRESSED, mousePressedHandler)
        pContent.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDraggedHandler)
        pContent.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler)
        // sometimes MOUSE_RELEASED is not delivered but the MOUSE_CLICKED..
        pContent.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseReleasedHandler)
        pContent.addEventHandler(TouchEvent.TOUCH_PRESSED, touchPressedHandler)
        pContent.addEventHandler(TouchEvent.TOUCH_MOVED, touchDraggedHandler)
        pContent.addEventHandler(TouchEvent.TOUCH_RELEASED, touchReleasedHandler)
        pContent.addEventHandler(ZoomEvent.ANY, zoomHandler)
        pContent.addEventHandler(ScrollEvent.SCROLL, scrollHandler)
    }

    private fun removeMouseHandlersFromContent(pContent: Node) {
        pContent.removeEventHandler(MouseEvent.MOUSE_PRESSED, mousePressedHandler)
        pContent.removeEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDraggedHandler)
        pContent.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler)
        // sometimes MOUSE_RELEASED is not delivered but the MOUSE_CLICKED..
        pContent.removeEventHandler(MouseEvent.MOUSE_CLICKED, mouseReleasedHandler)
        pContent.removeEventHandler(TouchEvent.TOUCH_PRESSED, touchPressedHandler)
        pContent.removeEventHandler(TouchEvent.TOUCH_MOVED, touchDraggedHandler)
        pContent.removeEventHandler(TouchEvent.TOUCH_RELEASED, touchReleasedHandler)
        pContent.removeEventHandler(ZoomEvent.ANY, zoomHandler)
        pContent.removeEventHandler(ScrollEvent.SCROLL, scrollHandler)
    }


    private fun startPanning(x: Double, y: Double) {
        cursor = Cursor.MOVE
        clickPosition = Point2D(x, y)
        windowPosAtClick = Point2D(getContentX(), getContentY())
    }

    private var gesture: GraphInputGesture? = null
    private var owner: Any? = null

    private fun activateGesture(pGesture: GraphInputGesture, pEvent: Event, pOwner: Any): Boolean {
        println("activateGesture -> $pGesture")
        val ret = internalActivateGesture(pGesture,pEvent,pOwner);
        println("activateGesture -> $pGesture --> $ret")
        return ret
    }

    private fun internalActivateGesture(pGesture: GraphInputGesture, pEvent: Event, pOwner: Any): Boolean {
        if (!canOverwrite(owner, pOwner)) {
            return false
        }
        if (canActivate(pGesture, pEvent)) {
            gesture = pGesture
            owner = pOwner
            return true
        }
        // ELSE:
        return false
    }

    private fun canActivate(pGesture: GraphInputGesture, pEvent: Event): Boolean {
        val current = gesture
        if (current === pGesture) {
            return true
        } else if (current == null) {
            val isTouch =
                pEvent is TouchEvent || pEvent is MouseEvent && (pEvent as MouseEvent).isSynthesized || pEvent is ScrollEvent && (pEvent as ScrollEvent).touchCount > 0
            if (!isTouch) {
                when (pGesture) {
                    GraphInputGesture.PAN -> return (pEvent is ScrollEvent && !pEvent.isControlDown
                            || pEvent is MouseEvent && pEvent.isSecondaryButtonDown)
                    GraphInputGesture.ZOOM -> return pEvent is ScrollEvent && pEvent.isControlDown
                    GraphInputGesture.SELECT, GraphInputGesture.CONNECT, GraphInputGesture.MOVE, GraphInputGesture.RESIZE -> return pEvent is MouseEvent && pEvent.isPrimaryButtonDown
                }
            } else {
                when (pGesture) {
                    GraphInputGesture.ZOOM -> return pEvent is ZoomEvent
                    GraphInputGesture.PAN -> return pEvent is TouchEvent && pEvent.touchCount > 1
                    GraphInputGesture.SELECT, GraphInputGesture.CONNECT, GraphInputGesture.MOVE, GraphInputGesture.RESIZE -> return true
                }
            }
        }
        return false
    }

    open fun finishGesture(pExpected: GraphInputGesture, pOwner: Any): Boolean {
        if (gesture === pExpected && (owner === pOwner || !isVisible(owner))) {
            gesture = null
            owner = null
            return true
        }
        return false
    }

    private fun canOverwrite(pExisting: Any?, pCandidate: Any?): Boolean {
        if (pExisting === pCandidate) {
            return true
        }
        return if (pCandidate == null) {
            false
        } else pExisting == null || !isVisible(pExisting)
    }

    private fun isVisible(pNode: Any?): Boolean {
        return if (pNode != null) {
            if (pNode is Node) {
                (pNode as Node).isVisible && (pNode as Node).parent != null && (pNode as Node).scene != null
            } else true
        } else false
        // ELSE: null
    }
}