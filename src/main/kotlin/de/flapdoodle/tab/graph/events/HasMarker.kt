package de.flapdoodle.tab.graph.events

import de.flapdoodle.tab.extensions.scaledChange
import javafx.beans.property.DoubleProperty
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.input.MouseEvent

interface HasMarker<M : IsMarker> {
  fun marker(): M

}