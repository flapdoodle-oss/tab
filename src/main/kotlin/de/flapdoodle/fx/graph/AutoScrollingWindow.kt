package de.flapdoodle.fx.graph

import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.event.ActionEvent
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.control.ScrollBar
import javafx.scene.input.MouseEvent
import javafx.util.Duration

open class AutoScrollingWindow : PanningWindow() {
    companion object {
        private val JUMP_PERIOD = Duration.millis(25.0)
    }

    private val baseJumpAmount = 1.0
    private val maxJumpAmount = 50.0
    private val jumpAmountIncreasePerJump = 0.5
    private val insetToBeginScroll = 1.0

    private var timeline: Timeline? = null
    private var isScrolling = false
    private var jumpDistance: Point2D? = null

    private var autoScrollingEnabled = true
    private var jumpsTaken = 0

    init {
        addEventFilter(MouseEvent.MOUSE_DRAGGED) { event: MouseEvent ->
            this.handleMouseDragged(
                event
            )
        }
    }

    override fun handlePanningMouseReleased(pEvent: MouseEvent) {
        super.handlePanningMouseReleased(pEvent!!)
        endScrolling()
    }

    open fun isAutoScrollingEnabled(): Boolean {
        return autoScrollingEnabled
    }

    open fun setAutoScrollingEnabled(pAutoScrollingEnabled: Boolean) {
        autoScrollingEnabled = pAutoScrollingEnabled
    }

    private fun handleMouseDragged(event: MouseEvent) {
        if (event.isPrimaryButtonDown && event.target is Node && !isScrollBar(event)) {
            jumpDistance = getDistanceToJump(event.x, event.y)
            if (jumpDistance == null) {
                jumpsTaken = 0
            } else if (!isScrolling && isAutoScrollingEnabled()) {
                startScrolling()
            }
        }
    }

    private fun isScrollBar(pEvent: MouseEvent): Boolean {
        if (pEvent.target is Node) {
            var n: Node? = pEvent.target as Node
            while (n != null) {
                if (n is ScrollBar) {
                    return true
                }
                n = n.parent
            }
        }
        return false
    }

    private fun getDistanceToJump(cursorX: Double, cursorY: Double): Point2D? {
        var jumpX = 0.0
        var jumpY = 0.0
        val baseAmount = baseJumpAmount
        val additionalAmount = jumpsTaken * jumpAmountIncreasePerJump
        val distance = Math.min(baseAmount + additionalAmount, maxJumpAmount)
        if (cursorX <= insetToBeginScroll) {
            jumpX = -distance
        } else if (cursorX >= width - insetToBeginScroll) {
            jumpX = distance
        }
        if (cursorY <= insetToBeginScroll) {
            jumpY = -distance
        } else if (cursorY >= height - insetToBeginScroll) {
            jumpY = distance
        }
        return if (jumpX == 0.0 && jumpY == 0.0) {
            null
        } else Point2D(Math.round(jumpX).toDouble(), Math.round(jumpY).toDouble())
    }

    private fun panBy(x: Double, y: Double) {
        if (x != 0.0 && y != 0.0) {
            panTo(getContentX() + x, getContentY() + y)
        } else if (x != 0.0) {
            panToX(getContentX() + x)
        } else if (y != 0.0) {
            panToY(getContentY() + y)
        }
    }

    private fun startScrolling() {
        isScrolling = true
        jumpsTaken = 0
        val frame = KeyFrame(AutoScrollingWindow.JUMP_PERIOD,
            { event: ActionEvent? ->
                if (isScrolling && jumpDistance != null) {
                    panBy(jumpDistance!!.x, jumpDistance!!.y)
                    jumpsTaken++
                }
            })
        timeline = Timeline()
        timeline!!.setCycleCount(Animation.INDEFINITE)
        timeline!!.getKeyFrames().add(frame)
        timeline!!.play()
    }

    private fun endScrolling() {
        isScrolling = false
        if (timeline != null) {
            timeline!!.stop()
        }
    }

}