package de.flapdoodle.tab.graph.events3

import javafx.event.EventTarget
import javafx.scene.Group
import javafx.scene.Node
import tornadofx.*

fun <M: IsMarker> EventTarget.markedGroup(marker: M, initialChildren: Iterable<Node>? = null, op: MarkedGroup<M>.() -> Unit = {}) =
    opcr(this, MarkedGroup(marker).apply { if (initialChildren != null) children.addAll(initialChildren) }, op)


class MarkedGroup<M: IsMarker>(private val marker: M) : Group(), HasMarker<M> {
  override fun marker() = marker
}