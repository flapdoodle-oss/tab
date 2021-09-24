package de.flapdoodle.fx.layout.helper

import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.shape.Line

class Cross(x: Double=0.0, y: Double=0.0) : Region() {
    init {
        setPrefSize(10.0,10.0)
        children.add(Line(-10.0,0.0,10.0,0.0).apply {
            stroke= Color.BLACK
            strokeWidth=0.5
        })
        children.add(Line(0.0, -10.0,0.0,10.0).apply {
            stroke= Color.BLACK
            strokeWidth=0.5
        })
        translateX=x
        translateY=y
        isMouseTransparent=true
    }
}