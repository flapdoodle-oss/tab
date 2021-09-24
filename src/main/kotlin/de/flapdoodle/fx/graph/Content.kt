package de.flapdoodle.fx.graph

import javafx.scene.layout.Border
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle
import tornadofx.add
import tornadofx.observableListOf
import kotlin.math.sin

class Content : Region() {
    init {
        children.add(Circle(0.0,0.0, 10.0/ sin(Math.PI/4.0)).apply {
            fill = Color.rgb(255,255,255,0.2)
            stroke=Color.BLACK
            strokeWidth=0.5
        })
        children.add(Circle(0.0,0.0, 100.0).apply {
            fill = Color.TRANSPARENT
            stroke=Color.BLACK
            strokeWidth=0.5
        })
        children.add(Circle(0.0,0.0, 300.0).apply {
            fill = Color.TRANSPARENT
            stroke=Color.BLACK
            strokeDashArray.addAll(5.0,5.0)
            strokeWidth=0.5
        })
        children.add(Rectangle().apply {
            fill = Color.rgb(255,0,0,0.2)
            x=0.0
            y=0.0
            width=10.0
            height=10.0
        })
        children.add(Rectangle().apply {
            fill = Color.rgb(255,255,0,0.2)
            x=-10.0
            y=-10.0
            width=10.0
            height=10.0
        })
        children.add(Line(0.0,0.0,100.0,0.0).apply {
            strokeWidth=1.0
            stroke=Color.RED
        })
        children.add(Line(0.0,0.0,0.0,100.0).apply {
            strokeWidth=1.0
            stroke=Color.GREEN
        })
        width=300.0
        height=300.0

        prefWidth = 300.0
        prefHeight = 300.0

        minWidth = 300.0
        minHeight = 300.0

        isMouseTransparent = true
    }
}