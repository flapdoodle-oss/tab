package de.flapdoodle.tab.graph.nodes.values

import de.flapdoodle.tab.graph.nodes.AbstractGraphNode
import de.flapdoodle.tab.graph.nodes.OtherAbstractGraphNode
import javafx.scene.control.TableView
import tornadofx.*

class ValuesNode : OtherAbstractGraphNode<TableView<Dummy>>(Table()) {

}