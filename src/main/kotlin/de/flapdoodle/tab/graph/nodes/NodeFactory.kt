package de.flapdoodle.tab.graph.nodes

import javafx.scene.Node

interface NodeFactory<T: Node> {
  fun newInstance(): T
}