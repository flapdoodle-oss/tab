package de.flapdoodle.fx.layout

import javafx.geometry.Insets
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop

object Nodes {
    fun region(width: Double, heigth: Double, color: Color): Region {
        return Region().apply {
            prefWidth = width
            prefHeight = heigth

            minWidth = width
            minHeight = heigth

            maxWidth = width
            maxHeight = heigth

            background = Background(BackgroundFill(color, CornerRadii(0.0), Insets.EMPTY))
        }
    }

    fun region(width: Double, heigth: Double, color: Color, second: Color): Region {
        return Region().apply {
            prefWidth = width
            prefHeight = heigth

            minWidth = width
            minHeight = heigth

            maxWidth = width
            maxHeight = heigth

            val colors =
                LinearGradient(0.0, 0.0, 1.0, 1.0, true, CycleMethod.NO_CYCLE,
                    Stop(0.0, color), Stop(1.0, second)
                )
            
            background = Background(BackgroundFill(colors, CornerRadii(0.0), Insets.EMPTY))
        }
    }
}