package de.flapdoodle.tab.graph.nodes.values

import de.flapdoodle.tab.data.Table
import de.flapdoodle.tab.graph.nodes.AbstractGraphNode
import javafx.beans.property.ObjectProperty
import javafx.beans.value.ObservableObjectValue
import javafx.scene.layout.VBox

class NewValuesNode(
    table: ObjectProperty<Table>
) : AbstractGraphNode<VBox>(TableNode(table))