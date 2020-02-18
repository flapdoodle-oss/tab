package de.flapdoodle.tab.graph.nodes

import javafx.event.EventTarget
import javafx.scene.Node

interface NodeFactory<T: Node> {
  fun newInstance(): T
}