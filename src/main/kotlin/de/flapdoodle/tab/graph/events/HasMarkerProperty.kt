package de.flapdoodle.tab.graph.events

import javafx.scene.Node

var Node.marker: IsMarker?
  get(): IsMarker? = HasMarkerProperty.markerOf(this)
  set(marker): Unit {
    HasMarkerProperty.setMarker(this, marker)
  }

object HasMarkerProperty {

  private object KEY

  fun markerOf(node: Node): IsMarker? {
    return node.properties[KEY] as IsMarker?
  }

  fun setMarker(node: Node, marker: IsMarker?) {
    node.properties[KEY] = marker
  }

}