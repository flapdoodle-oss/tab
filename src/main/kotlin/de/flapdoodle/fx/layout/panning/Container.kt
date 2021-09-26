package de.flapdoodle.fx.layout.panning

import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane

class Container : Region() {
    init {
        //isMouseTransparent=false
        isPickOnBounds = false
    }
    
    private var content: Region?=null

    fun setContent(content: Region?) {
        this.content=content

        children.clear()
        if (content!=null) {
            children.add(content)
        }
    }

    override fun computePrefWidth(height: Double): Double {
        return content?.prefWidth(height) ?: 0.0
    }

    override fun computePrefHeight(width: Double): Double {
        return content?.prefHeight(height) ?: 0.0
    }

    override fun computeMinWidth(height: Double): Double {
        return content?.minWidth(height) ?: 0.0
    }

    override fun computeMinHeight(width: Double): Double {
        return content?.minHeight(height) ?: 0.0
    }

    override fun computeMaxWidth(height: Double): Double {
        return content?.minWidth(height) ?: 0.0
    }

    override fun computeMaxHeight(width: Double): Double {
        return content?.minHeight(height) ?: 0.0
    }
}