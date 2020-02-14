package de.flapdoodle.tab.graph.events

import javafx.scene.Node

object HasMarkerProperty {

  private object KEY

  fun markerOf(node: Node): IsMarker? {
    return node.properties[KEY] as IsMarker?
  }

  fun setMarker(node: Node, marker: IsMarker) {
    node.properties[KEY] = marker
  }

}