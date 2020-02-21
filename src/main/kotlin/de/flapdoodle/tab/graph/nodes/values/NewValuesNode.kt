package de.flapdoodle.tab.graph.nodes.values

import de.flapdoodle.tab.data.Table
import de.flapdoodle.tab.graph.nodes.AbstractGraphNode
import javafx.beans.property.ObjectProperty
import javafx.beans.value.ObservableObjectValue
import javafx.beans.value.ObservableValue
import javafx.beans.value.WritableObjectValue
import javafx.scene.layout.VBox

class NewValuesNode<W>(
    table: W
) : AbstractGraphNode<VBox>(TableNode(table))
where W : ObservableValue<Table>,
      W : WritableObjectValue<Table>