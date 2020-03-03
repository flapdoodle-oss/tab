package de.flapdoodle.tab.graph.events

import javafx.scene.Node

var Node.marker: IsMarker?
  get(): IsMarker? = HasMarkerProperty.markerOf(this)
  set(marker): Unit {
    HasMarkerProperty.setMarker(this, marker)
  }

object HasMarkerProperty {

  private object KEY

  fun markerOf(node: Node, searchInParent: Boolean = false): IsMarker? {
    val marker = node.properties[KEY] as IsMarker?
    if (marker==null && searchInParent && node.parent!=null) {
      return markerOf(node.parent, searchInParent)
    }
    return marker
  }

  fun setMarker(node: Node, marker: IsMarker?) {
    node.properties[KEY] = marker
  }

}