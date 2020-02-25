package de.flapdoodle.tab.graph.nodes.values

import de.flapdoodle.tab.data.Data
import de.flapdoodle.tab.data.HasColumns
import de.flapdoodle.tab.data.TableDef
import de.flapdoodle.tab.graph.nodes.AbstractGraphNode
import de.flapdoodle.tab.graph.nodes.ColumnValueChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox

class TableDefGraphNode(
    table: ObservableValue<out HasColumns>,
    data: ObservableValue<Data>,
    changeListener: ColumnValueChangeListener?
) : AbstractGraphNode<BorderPane>(TableDefNode(table, data, changeListener))