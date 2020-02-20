package de.flapdoodle.tab.graph.nodes.values

import de.flapdoodle.tab.data.Table
import de.flapdoodle.tab.graph.nodes.AbstractGraphNode
import javafx.beans.value.ObservableObjectValue
import javafx.scene.layout.VBox

class NewValuesNode(
    private val table: ObservableObjectValue<Table>
) : AbstractGraphNode<VBox>(TableNode(table))