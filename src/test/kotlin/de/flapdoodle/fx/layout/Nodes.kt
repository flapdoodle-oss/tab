package de.flapdoodle.fx.layout

import javafx.geometry.Insets
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.Region
import javafx.scene.paint.Color

object Nodes {
    fun region(width: Double, heigth: Double, color: Color): Region {
        return Region().apply {
            prefWidth = width
            prefHeight = heigth
            
            minWidth = width
            minHeight = heigth

            maxWidth = width
            maxHeight = heigth

            background= Background(BackgroundFill(color, CornerRadii(0.0), Insets.EMPTY))
        }
    }
}