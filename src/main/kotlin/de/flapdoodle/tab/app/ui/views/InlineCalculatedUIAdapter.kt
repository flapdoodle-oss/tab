package de.flapdoodle.tab.app.ui.views

import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.ui.ModelChangeListener
import de.flapdoodle.tab.app.ui.views.calculations.CalculationsPane
import de.flapdoodle.tab.app.ui.views.calculations.ValuesPane

class InlineCalculatedUIAdapter<K: Comparable<K>>(
    node: Node.Calculated<K>,
    val modelChangeListener: ModelChangeListener
) : NodeUIAdapter() {
    val nodeId = node.id

    val calculationsPane = CalculationsPane(node, modelChangeListener)
    val valuesPane = ValuesPane(node)

    init {
        children.add(calculationsPane)
        children.add(valuesPane)
    }

    override fun update(node: Node) {
        require(node is Node.Calculated<out Comparable<*>>) { "wrong type $node" }
        require(node.id == nodeId) {"wrong node: ${node.id} != $nodeId"}

        calculationsPane.update(node as Node.Calculated<K>)
        valuesPane.update(node)
    }
}