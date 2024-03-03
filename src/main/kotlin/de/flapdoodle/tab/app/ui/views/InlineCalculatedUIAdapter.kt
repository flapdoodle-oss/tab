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

//    val valuesModel = SimpleObjectProperty(node.values.values)
//    val valuesPanel = WeightGridTable(
//        model = valuesModel,
//        indexOf = SingleValue<out Any>::id,
//        columns = listOf(
//            WeightGridTable.Column(weight = 1.0, nodeFactory = { Label(it.name) to WeightGridTable.ChangeListener {  } }),
//            WeightGridTable.Column(weight = 0.1, nodeFactory = { Label("=") to WeightGridTable.ChangeListener {  } }),
//            WeightGridTable.Column(weight = 10.0, nodeFactory = { typedlabel(it) to WeightGridTable.ChangeListener {  } })),
//    )

    init {
        children.add(calculationsPane)
        children.add(valuesPane)
    }

//    private fun <T: Any> typedlabel(value: SingleValue<T>): TypedLabel<T> {
//        return TypedLabel(value.valueType).apply {
//            set(value.value)
//        }
//    }

    override fun update(node: Node) {
        require(node is Node.Calculated<out Comparable<*>>) { "wrong type $node" }
        require(node.id == nodeId) {"wrong node: ${node.id} != $nodeId"}
        valuesPane.update(node as Node.Calculated<K>)
    }
}